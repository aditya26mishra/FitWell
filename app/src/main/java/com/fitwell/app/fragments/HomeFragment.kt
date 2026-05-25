package com.fitwell.app.fragments

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.fitwell.app.R
import com.fitwell.app.database.AppDatabase
import com.fitwell.app.utils.Dates
import com.fitwell.app.utils.Session
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val db = AppDatabase.get(requireContext())
        val uid = Session(requireContext()).userId

        // Greeting
        val tvGreet = view.findViewById<TextView>(R.id.tvGreeting)
        val tvDate = view.findViewById<TextView>(R.id.tvDate)
        tvDate.text = Dates.pretty(Dates.today())

        lifecycleScope.launch {
            db.userDao().getByIdFlow(uid).collectLatest { user ->
                user ?: return@collectLatest
                val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
                val greet = when { hour < 12 -> "Good morning"; hour < 17 -> "Good afternoon"; else -> "Good evening" }
                tvGreet.text = "$greet, ${user.name.split(" ").first()}"
            }
        }

        val tabLayout = view.findViewById<TabLayout>(R.id.homeTabLayout)
        val viewPager = view.findViewById<ViewPager2>(R.id.homeViewPager)
        viewPager.adapter = HomePagerAdapter(this)
        viewPager.offscreenPageLimit = 3

        val tabTitles = listOf("Calories", "Steps", "Workout")
        val tabIcons = listOf(R.drawable.ic_calories, R.drawable.ic_steps, R.drawable.ic_workout)
        TabLayoutMediator(tabLayout, viewPager) { tab, pos ->
            tab.text = tabTitles[pos]
            tab.setIcon(tabIcons[pos])
        }.attach()

        // Hamburger opens drawer
        val btnMenu = view.findViewById<View>(R.id.btnMenu)
        btnMenu.setOnClickListener {
            val drawer = requireActivity().findViewById<androidx.drawerlayout.widget.DrawerLayout>(R.id.drawerLayout)
            drawer.openDrawer(androidx.core.view.GravityCompat.START)
        }
    }
}

class HomePagerAdapter(f: Fragment) : FragmentStateAdapter(f) {
    override fun getItemCount() = 3
    override fun createFragment(pos: Int): Fragment = when(pos) {
        0 -> CalorieHomeFragment()
        1 -> StepFragment()
        else -> WorkoutFragment()
    }
}
