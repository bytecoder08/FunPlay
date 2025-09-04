package com.bytecoder.funplay.ui.explorer

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bytecoder.funplay.databinding.FragmentExplorerBinding
import com.bytecoder.funplay.viewmodel.ExplorerViewModel

class ExplorerFragment : Fragment() {
    private var _bind: FragmentExplorerBinding? = null
    private val bind get() = _bind!!
    private val vm: ExplorerViewModel by viewModels()

    private val pickFolder = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            it.data?.data?.also { uri -> requireContext().contentResolver.takePersistableUriPermission(
                uri, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            ); vm.list(uri) }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentExplorerBinding.inflate(inflater, container, false).also { _bind = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = ExplorerAdapter()
        bind.recycler.layoutManager = LinearLayoutManager(requireContext())
        bind.recycler.adapter = adapter

        bind.btnPickFolder.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                putExtra(DocumentsContract.EXTRA_INITIAL_URI, Uri.parse("content://com.android.externalstorage.documents/document/primary:"))
            }
            pickFolder.launch(intent)
        }

        vm.entries.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }
    }

    override fun onDestroyView() { _bind = null; super.onDestroyView() }
}
