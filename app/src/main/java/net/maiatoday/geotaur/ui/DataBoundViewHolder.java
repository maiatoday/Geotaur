/*
 * MIT License
 *
 * Copyright (c) [2016] [Maia Grotepass]
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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