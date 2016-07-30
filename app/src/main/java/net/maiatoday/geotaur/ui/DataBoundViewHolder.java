package net.maiatoday.geotaur.ui;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import net.maiatoday.geotaur.BR;

/**
 * View Holder for recycler view that accommodates data binding
 * Created by maia on 2016/03/17.
 */
public class DataBoundViewHolder extends RecyclerView.ViewHolder {
    protected ViewDataBinding mBinding;

    public DataBoundViewHolder(ViewDataBinding binding) {
        super(binding.getRoot());
        mBinding = binding;
    }
    public ViewDataBinding getBinding() {
        return mBinding;
    }

    public void bindTo(Object obj, Object handler) {
        mBinding.setVariable(BR.data, obj);
        mBinding.setVariable(BR.handler, handler);
        mBinding.executePendingBindings();
    }

    public void setClicklistener(View.OnClickListener onClickListener) {
        mBinding.getRoot().setOnClickListener(onClickListener);
    }
}