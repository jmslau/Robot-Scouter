package com.supercilex.robotscouter.feature.templates

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.supercilex.robotscouter.Refreshable
import com.supercilex.robotscouter.SignInResolver
import com.supercilex.robotscouter.TemplateListFragmentBridge
import com.supercilex.robotscouter.TemplateListFragmentCompanion
import com.supercilex.robotscouter.TemplateListFragmentCompanion.Companion.TAG
import com.supercilex.robotscouter.core.data.TAB_KEY
import com.supercilex.robotscouter.core.data.defaultTemplateId
import com.supercilex.robotscouter.core.data.getTabId
import com.supercilex.robotscouter.core.data.isSignedIn
import com.supercilex.robotscouter.core.data.model.addTemplate
import com.supercilex.robotscouter.core.model.TemplateType
import com.supercilex.robotscouter.core.ui.FragmentBase
import com.supercilex.robotscouter.core.ui.LifecycleAwareLazy
import com.supercilex.robotscouter.core.ui.OnBackPressedListener
import com.supercilex.robotscouter.core.ui.RecyclerPoolHolder
import com.supercilex.robotscouter.core.ui.longSnackbar
import com.supercilex.robotscouter.core.ui.onDestroy
import com.supercilex.robotscouter.core.unsafeLazy
import kotlinx.android.synthetic.main.fragment_template_list.*
import org.jetbrains.anko.find
import com.supercilex.robotscouter.R as RC

internal class TemplateListFragment : FragmentBase(), TemplateListFragmentBridge, Refreshable,
        View.OnClickListener, OnBackPressedListener, RecyclerPoolHolder {
    override val recyclerPool by LifecycleAwareLazy { RecyclerView.RecycledViewPool() }

    val pagerAdapter: TemplatePagerAdapter by unsafeLazy {
        object : TemplatePagerAdapter(this@TemplateListFragment) {
            override fun onDataChanged() {
                super.onDataChanged()
                if (currentScouts.isEmpty()) {
                    fab.hide()
                } else {
                    fab.show()
                }
            }
        }
    }
    private val fab by unsafeLazy { requireActivity().find<FloatingActionButton>(RC.id.fab) }
    private val appBar by unsafeLazy { requireActivity().find<AppBarLayout>(RC.id.appBar) }
    private val tabs by LifecycleAwareLazy {
        val tabs = TabLayout(requireContext()).apply {
            id = RC.id.tabs
            tabMode = TabLayout.MODE_SCROLLABLE
        }
        appBar.addView(
                tabs, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        tabs
    } onDestroy {
        appBar.removeView(it)
    }

    init {
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = View.inflate(context, R.layout.fragment_template_list, null)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tabs // Force init
        viewPager.adapter = pagerAdapter
        tabs.setupWithViewPager(viewPager)
        fab.setOnClickListener(this)

        handleArgs(arguments, savedInstanceState)
    }

    override fun refresh() {
        childFragmentManager.fragments
                .filterIsInstance<TemplateFragment>()
                .singleOrNull { pagerAdapter.currentTabId == it.dataId }
                ?.refresh()
    }

    override fun onStop() {
        // This has to be done in onStop so fragment transactions from the view pager can be
        // committed. Only reset the adapter if the user is switching destinations.
        if (isDetached) pagerAdapter.reset()
        super.onStop()
    }

    override fun handleArgs(args: Bundle) = handleArgs(args, null)

    private fun handleArgs(args: Bundle?, savedInstanceState: Bundle?) {
        val templateId = getTabId(args)
        if (templateId != null) {
            pagerAdapter.currentTabId = TemplateType.coerce(templateId)?.let {
                longSnackbar(viewPager, R.string.template_added_message)
                addTemplate(it).also { defaultTemplateId = it }
            } ?: templateId

            args?.remove(TAB_KEY)
        } else {
            getTabId(savedInstanceState)?.let { pagerAdapter.currentTabId = it }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) =
            inflater.inflate(R.menu.template_list_menu, menu)

    override fun onSaveInstanceState(outState: Bundle) = pagerAdapter.onSaveInstanceState(outState)

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (!isSignedIn) {
            (activity as SignInResolver).showSignInResolution()
            return false
        }

        when (item.itemId) {
            R.id.action_new_template -> NewTemplateDialog.show(childFragmentManager)
            R.id.action_share -> TemplateSharer.shareTemplate(
                    this,
                    checkNotNull(pagerAdapter.currentTabId),
                    checkNotNull(pagerAdapter.currentTab?.text?.toString())
            )
            else -> return false
        }
        return true
    }

    override fun onClick(v: View) {
        if (v.id == RC.id.fab) {
            AddMetricDialog.show(childFragmentManager)
        } else {
            childFragmentManager.fragments
                    .filterIsInstance<TemplateFragment>()
                    .singleOrNull { pagerAdapter.currentTabId == it.dataId }
                    ?.onClick(v)
        }
    }

    fun onTemplateCreated(id: String) {
        pagerAdapter.currentTabId = id
        longSnackbar(
                viewPager,
                R.string.template_added_title,
                RC.string.template_set_default_title
        ) { defaultTemplateId = id }
    }

    override fun onBackPressed(): Boolean =
            childFragmentManager.fragments.any { it is OnBackPressedListener && it.onBackPressed() }

    companion object : TemplateListFragmentCompanion {
        override fun getInstance(
                manager: FragmentManager,
                args: Bundle?
        ): TemplateListFragment {
            val instance = manager.findFragmentByTag(TAG) as TemplateListFragment?
                    ?: TemplateListFragment()
            return instance.apply { arguments = args }
        }
    }
}
