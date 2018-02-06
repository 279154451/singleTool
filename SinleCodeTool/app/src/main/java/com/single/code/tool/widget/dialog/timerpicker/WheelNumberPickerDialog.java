package com.single.code.tool.widget.dialog.timerpicker;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.TextView;

import com.single.code.tool.R;
import com.single.code.tool.widget.dialog.timerpicker.spinnerwheel.AbstractWheel;
import com.single.code.tool.widget.dialog.timerpicker.spinnerwheel.OnWheelChangedListener;
import com.single.code.tool.widget.dialog.timerpicker.spinnerwheel.OnWheelScrollListener;
import com.single.code.tool.widget.dialog.timerpicker.spinnerwheel.WheelVerticalView;
import com.single.code.tool.widget.dialog.timerpicker.spinnerwheel.adapters.NumericWheelAdapter;


/**
 * 数字选择器
 */
public class WheelNumberPickerDialog extends Dialog {
    public WheelNumberPickerDialog(Context context, int theme) {
        super(context, theme);
    }

    public static class Builder {
        private Context context;
        private boolean wheelScrolled = false;
        private String title;
        private int hourValue, minValue, secValue;
        private INumberPickerDialogResults numberPickerDialogResults;

        //对话框按钮监听事件
        private OnClickListener
                backButtonClickListener, sureButtonClickListener;

        public Builder(Context context, INumberPickerDialogResults numberPickerDialogResults) {
            this.context = context;
            this.numberPickerDialogResults = numberPickerDialogResults;
        }

        //设置back按钮的事件和文本
        public Builder setBackButton(OnClickListener listener) {
            this.backButtonClickListener = listener;
            return this;
        }


        //设置sure按钮的事件和文本
        public Builder setSureButton(OnClickListener listener) {
            this.sureButtonClickListener = listener;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        //设置小时
        public Builder setHourValue(int hourValue) {
            this.hourValue = hourValue;
            return this;
        }

        //设置分钟
        public Builder setMinuteValue(int minValue) {
            this.minValue = minValue;
            return this;
        }

        //设置秒
        public Builder setSecondValue(int secValue) {
            this.secValue = secValue;
            return this;
        }


        //创建自定义的对话框
        public WheelNumberPickerDialog create() {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // 实例化自定义的对话框主题
            final WheelNumberPickerDialog dialog = new WheelNumberPickerDialog(context, R.style.CustomDialog);
            View layout = inflater.inflate(R.layout.dialog_wheel_number_picker, null);
            dialog.addContentView(layout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            dialog.setContentView(layout);

            ((TextView) layout.findViewById(R.id.dialog_wheel_number_picker_title_text)).setText(title);

            WheelVerticalView hourView = (WheelVerticalView) layout.findViewById(R.id.dialog_wheel_number_picker_hour);
            WheelVerticalView minView = (WheelVerticalView) layout.findViewById(R.id.dialog_wheel_number_picker_minute);
            WheelVerticalView secondView = (WheelVerticalView) layout.findViewById(R.id.dialog_wheel_number_picker_second);


            if (null != hourView) {
                hourView.setViewAdapter(new NumericWheelAdapter(context, 0, 23));
                hourView.setCurrentItem(0);
                hourView.addScrollingListener(hourScrolledListener);
                hourView.addChangingListener(hourChangedListener);
                hourView.setCyclic(true);
                hourView.setInterpolator(new AnticipateOvershootInterpolator());
            }

            if (null != minView) {
                minView.setViewAdapter(new NumericWheelAdapter(context, 0, 59));
                minView.setCurrentItem(0);
                minView.addScrollingListener(minScrolledListener);
                minView.addChangingListener(minChangedListener);
                minView.setCyclic(true);
                minView.setInterpolator(new AnticipateOvershootInterpolator());
            }

            if (null != secondView) {
                secondView.setViewAdapter(new NumericWheelAdapter(context, 0, 59));
                secondView.setCurrentItem(0);
                secondView.addScrollingListener(secondScrolledListener);
                secondView.addChangingListener(secondChangedListener);
                secondView.setCyclic(true);
                secondView.setInterpolator(new AnticipateOvershootInterpolator());
            }

            //取消按钮
            TextView bckButton = ((TextView) layout.findViewById(R.id.dialog_wheel_number_picker_abandon_btn));
            if (backButtonClickListener != null) {
                bckButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        backButtonClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
                    }
                });
            }

            //确定按钮
            TextView sureButton = ((TextView) layout.findViewById(R.id.dialog_wheel_number_picker_sure_btn));
            sureButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    numberPickerDialogResults.onConfirm(hourValue, minValue, secValue);
                    sureButtonClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
                }
            });
            return dialog;
        }


        //小时
        OnWheelScrollListener hourScrolledListener = new OnWheelScrollListener() {
            public void onScrollingStarted(AbstractWheel wheel) {
                wheelScrolled = true;
            }

            public void onScrollingFinished(AbstractWheel wheel) {
                wheelScrolled = false;
                hourValue = wheel.getCurrentItem();
            }
        };

        private OnWheelChangedListener hourChangedListener = new OnWheelChangedListener() {
            public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
                if (!wheelScrolled) {

                }
            }
        };


        //分钟
        OnWheelScrollListener minScrolledListener = new OnWheelScrollListener() {
            public void onScrollingStarted(AbstractWheel wheel) {
                wheelScrolled = true;
            }

            public void onScrollingFinished(AbstractWheel wheel) {
                wheelScrolled = false;
                minValue = wheel.getCurrentItem();
            }
        };

        private OnWheelChangedListener minChangedListener = new OnWheelChangedListener() {
            public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
                if (!wheelScrolled) {

                }
            }
        };

        //秒
        OnWheelScrollListener secondScrolledListener = new OnWheelScrollListener() {
            public void onScrollingStarted(AbstractWheel wheel) {
                wheelScrolled = true;
            }

            public void onScrollingFinished(AbstractWheel wheel) {
                wheelScrolled = false;
                secValue = wheel.getCurrentItem();
            }
        };

        private OnWheelChangedListener secondChangedListener = new OnWheelChangedListener() {
            public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
                if (!wheelScrolled) {

                }
            }
        };
    }

    public interface INumberPickerDialogResults {
        void onConfirm(int hourValue, int minValue, int secValue);
    }
}
