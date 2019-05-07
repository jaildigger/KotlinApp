package com.ec.expresscheck.controller.loyalty

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.ec.expresscheck.R
import com.ec.expresscheck.controller.base.BackPressController
import com.ec.expresscheck.model.Loyalty
import com.ec.expresscheck.model.Reward
import com.ec.expresscheck.rest.Services
import com.ec.expresscheck.util.*
import kotlinx.android.synthetic.main.controller_rewards_list_of_new.*
import kotlinx.android.synthetic.main.item_new_reward.view.*
import org.joda.time.LocalDate

class RewardsListController(bundle: Bundle?) : BackPressController(bundle) {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedViewState: Bundle?): View {
        return inflater.inflate(R.layout.controller_rewards_list_of_new, container, false)
    }

    override fun onOtherMenuItemSelected(item: MenuItem): Boolean {
        return true
    }

    constructor(loyalty: Loyalty) : this(BundleBuilder(Bundle()).putSerializable(LOYALTY, loyalty).build())

    companion object {
        const val LOYALTY = "loyalty"
    }

    val loyalty: Loyalty by lazy { args.getSerializable(LOYALTY) as Loyalty }


    override fun onViewCreated(view: View) {
        super.onViewCreated(view)
        updateUi()
    }

    private fun toggleProgress(show: Boolean) = progress_bar.manageVisible(show)


    private fun updateUi() {
        getRewards()
    }

    private val dateFormat = "yyyy-MM-dd HH:mm:ss"

    private fun getRewards() {
        toggleProgress(true)
        Services.loyaltyApi.getRewards(loyalty.venue_id, LocalDate.now().toString(dateFormat)).enqueue {
            onResponse = {
                toggleProgress(false)
                if (it.isSuccessful) {
                    val response = it.body()
                    val rewards = response?.rewards ?: listOf()
                    if (rewards.isNotEmpty()) {
                        val adapter = RewardsAdapter(rewards) { reward ->
                            router.pushController(navigate(RewardInfoController(loyalty, reward)))
                        }
                        rewards_list.layoutManager = LinearLayoutManager(activity)
                        rewards_list.adapter = adapter
                        rewards_list.visible()
                    } else {
                        rewards_list.gone()
                    }

                } else {
                    activity?.showError(it)
                }
            }
        }
    }


    inner class RewardsAdapter(var items: List<Reward>, private val listener: (Reward) -> Unit) :
        androidx.recyclerview.widget.RecyclerView.Adapter<RewardsAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(parent.inflate(R.layout.item_new_reward))

        override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position], listener)

        override fun getItemCount() = items.size

        inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
            fun bind(item: Reward, listener: (Reward) -> Unit) = with(itemView) {
                reward_logo?.loadUrl(
                    loyalty.venue_logo
                )
                reward_title?.text = item.title
                button_redeem?.setOnClickListener { listener(item) }
                return@with
            }


        }
    }

}