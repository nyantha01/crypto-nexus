package com.example.cryptocurrency.fragments
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cryptocurrency.R
import com.example.cryptocurrency.adapter.TransactionAdapter
import com.example.cryptocurrency.databinding.FragmentAddTransactionBinding
import com.example.cryptocurrency.model.CryptoDetails
import com.example.cryptocurrency.network_detector.NetworkDetector
import com.example.cryptocurrency.view_model.CryptoViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddTransactionFragment : Fragment() {
    private lateinit var binding: FragmentAddTransactionBinding
    private lateinit var transAdapter: TransactionAdapter
    private val networkDetector by lazy { context?.let { NetworkDetector(it) } }
    private val transViewModel: CryptoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_add_transaction, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addTransCircular.isVisible = true
        binding.searchView.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.grey3
            )
        )
        binding.searchCard.setBackgroundResource(R.drawable.search_bar_background)
        val emptyList = emptyList<CryptoDetails>()
        binding.noConnection.isVisible = false
        networkDetector?.observe(viewLifecycleOwner) { isConnected ->
            if (isConnected) {
                setupRecyclerView()
                transViewModel.marketLiveData.observe(viewLifecycleOwner) { cryptos ->
                    transAdapter.differ.submitList(cryptos)
                    filterItem(cryptos)
                    binding.addTransCircular.isVisible = false
                }
                selectedCrypto()
            } else {
                transViewModel.getRoomCryptos().observe(viewLifecycleOwner) { cryptos ->
                    transAdapter.differ.submitList(cryptos)
                    filterItem(cryptos)
                    binding.addTransCircular.isVisible = false
                }
                setupRecyclerView()
                selectedCrypto()
//                    filterItem(emptyList)

            }
        }

        binding.backKey.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupRecyclerView() {
        binding.recView.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            transAdapter = TransactionAdapter()
            adapter = transAdapter
        }
    }

    private fun filterItem(list: List<CryptoDetails>) {
        binding.searchView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(query: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(query: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (query != null) {
                    transAdapter.filter.filter(query)
                    transAdapter.updateFilteredList(list)

                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
    }

    private fun selectedCrypto() {
        transAdapter.onItemClick = { crypto ->
            val bundle = Bundle()
            bundle.apply {
                putString("name", crypto.name)
                putString("symbol", crypto.symbol)
                putString("id", crypto.id.toString())
                putString("price", crypto.quote.USD.price.toString())
                putString("change24h", crypto.quote.USD.percent_change_24h.toString())
            }
            findNavController().navigate(R.id.action_addTransactionFragment_to_transactionAmountFragment, bundle)
        }
    }


}