package com.example.cryptocurrency.fragments
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.cryptocurrency.R
import com.example.cryptocurrency.adapter.BlockSpanAdapter
import com.example.cryptocurrency.databinding.FragmentNftsBinding
import com.example.cryptocurrency.network_detector.NetworkDetector
import com.example.cryptocurrency.view_model.CryptoViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NftsFragment : Fragment() {
    private lateinit var binding: FragmentNftsBinding
    private lateinit var blockSpanAdapter: BlockSpanAdapter
    private val blockSpanViewModel : CryptoViewModel by viewModels()
    private val networkDetector  by lazy { context?.let { NetworkDetector(it) } }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_nfts,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.nftCircular.isVisible = true
        setupRecyclerView()
        networkDetector?.observe(viewLifecycleOwner){ isConnected ->
            if (isConnected){
                blockSpanViewModel.blockSpanNftLiveData.observe(viewLifecycleOwner){ result ->
                    blockSpanAdapter.differ.submitList(result)
                    binding.nftCircular.isVisible = false
                }
                binding.noConnection.isVisible = false
                blockSpanViewModel.getBlockSpanNfts()
            }else{
                setupRecyclerView()
                binding.noConnection.isVisible = true
            }
        }

        onItemClick()
    }

    private fun setupRecyclerView(){
        binding.recView.apply {
            layoutManager = GridLayoutManager(requireContext(),2,GridLayoutManager.VERTICAL,false)
            blockSpanAdapter = BlockSpanAdapter()
            adapter = blockSpanAdapter
        }
    }

    private fun onItemClick(){
        blockSpanAdapter.onItemClick = { result,stats ->
            val bundle = Bundle()
            bundle.apply {
                putString("studio",result.key)
                putString("project",result.name)
                putString("description",result.description)
                putString("profileImage",result.image_url)
                putString("featuredImage",result.featured_image_url)
                putString("totalSupply",stats.total_supply.toString())
                putString("owners",stats.num_owners.toString())
                putString("totalVolume",stats.total_volume.toString())
                putString("exchangeUrl",result.exchange_url)
                findNavController().navigate(R.id.blockSpanDetailFragment,bundle)
            }
        }
    }
}