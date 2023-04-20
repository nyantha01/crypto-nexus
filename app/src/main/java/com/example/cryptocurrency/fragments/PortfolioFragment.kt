package com.example.cryptocurrency.fragments
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cryptocurrency.R
import com.example.cryptocurrency.adapter.TransactionRoomAdapter
import com.example.cryptocurrency.databinding.FragmentPortfolioBinding
import com.example.cryptocurrency.utils.SwipeToDeleteCallback
import com.example.cryptocurrency.view_model.CryptoViewModel
import com.google.android.material.snackbar.BaseTransientBottomBar.ANIMATION_MODE_SLIDE
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PortfolioFragment : Fragment() {
    private lateinit var binding: FragmentPortfolioBinding
    private lateinit var transactionRoomAdapter: TransactionRoomAdapter
    private val transactionRoomViewModel: CryptoViewModel by viewModels()
    private lateinit var swipeToDeleteCallback: SwipeToDeleteCallback
    var isHide = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_portfolio, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recView.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true)
            transactionRoomAdapter = TransactionRoomAdapter()
            adapter = transactionRoomAdapter
        }
        swipeToDeleteCallback =
            SwipeToDeleteCallback(transactionRoomViewModel, transactionRoomAdapter)
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(binding.recView)

        transactionRoomViewModel.roomTransaction()
            .observe(viewLifecycleOwner) { transaction ->
                transactionRoomAdapter.differ.submitList(transaction)
            }

        binding.addAsset.setOnClickListener {
            findNavController().navigate(R.id.addTransactionFragment)
        }

        binding.hideBtn.setOnClickListener {
            binding.apply {
                balance.isVisible = true
                hideBtn.isVisible = false
                showBtn.isVisible = true
                unseen.isVisible = false
            }
        }
        binding.showBtn.setOnClickListener {
            binding.apply {
                balance.isVisible = false
                hideBtn.isVisible = true
                showBtn.isVisible = false
                unseen.isVisible = true
            }
        }
    }


}