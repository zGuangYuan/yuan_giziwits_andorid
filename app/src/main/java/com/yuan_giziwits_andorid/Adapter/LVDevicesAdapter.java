package com.yuan_giziwits_andorid.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.enumration.GizWifiDeviceNetStatus;
import com.yuan_giziwits_andorid.R;

import java.util.List;

/**
 *
 */
public class LVDevicesAdapter extends BaseAdapter {
    //上下文
    private Context mContent;
    //存储设备的一个集合
    private List<GizWifiDevice> gizWifiDevices;
    //管理布局器LayoutInflater，它主要用于加载布局
    private LayoutInflater mLayoutInflater;

    public LVDevicesAdapter(Context mContent, List<GizWifiDevice> gizWifiDevices) {
        this.mContent = mContent;
        this.gizWifiDevices = gizWifiDevices;

        mLayoutInflater= (LayoutInflater) mContent.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return gizWifiDevices.size();
    }
    @Override
    public Object getItem(int i) {
        return gizWifiDevices.get(i);
    }
    @Override
    public long getItemId(int i) {
        return i;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        //初始化ViewHolder
        ViewHolderListView viewHolderListView = null;
        View view1;
        //拿到绑定的设备
        GizWifiDevice device = gizWifiDevices.get(i);
        //view为空，从为加载过，则从布局管理器中加载
        if(view == null){
            view1 = mLayoutInflater.inflate(R.layout.lv_item_devicelist,null);
            //创建一个ViewHolderListView 对象，其实就是创建了几个变量而已
            viewHolderListView =new ViewHolderListView();

            //绑定控件,控件实例化
            viewHolderListView.mTvDeviceName = view1.findViewById(R.id.tv_CurrentDevicesNane_ID);
            viewHolderListView.mTvDeviceState =view1.findViewById(R.id.tv_Online_state_ID);
            viewHolderListView.mIvDeviceIcon =view1.findViewById(R.id.iv_devices_ID);
            viewHolderListView.mIvNext =view1.findViewById(R.id.iv_device_next_ID);

            view1.setTag(viewHolderListView);
        }else{  //

            view1=view;
            viewHolderListView = (ViewHolderListView) view1.getTag();
        }
        //判断设备在线状态
        if(device.getNetStatus() == GizWifiDeviceNetStatus.GizDeviceOffline)
        {
            //设置为离线，淡化文本tab_panel_divider
            viewHolderListView.mTvDeviceState.setText("离线");
            viewHolderListView.mTvDeviceState.setTextSize(20);

            viewHolderListView.mTvDeviceState.setTextColor(mContent.getResources().getColor(R.color.tab_panel_divider));
            //隐藏箭头
            viewHolderListView.mIvNext.setVisibility(View.INVISIBLE);
        }else{
            //设备在线，则进一步分析是本地还是远程在线
            if(device.isLAN()){
                viewHolderListView.mTvDeviceState.setText("局域网在线");
            }else{
                viewHolderListView.mTvDeviceState.setText("远程在线");
            }
            //设置字体大小
            viewHolderListView.mTvDeviceState.setTextSize(12);
            //设置颜色为深色
            viewHolderListView.mTvDeviceState.setTextColor(mContent.getResources().getColor(R.color.black_color));
            //显示箭头
            viewHolderListView.mIvNext.setVisibility(View.VISIBLE);

        }
        //判断设备是否有别名，如果有则优先显示别名
        if(device.getAlias().isEmpty()){
            //显示产品名字
            viewHolderListView.mTvDeviceName.setText(device.getProductName());
        }else{
            //显示别名
            viewHolderListView.mTvDeviceName.setText(device.getAlias());
        }




        return view1;
    }
    //内部类
    private class ViewHolderListView{
        //设备图标，和下一个图标
        ImageView mIvDeviceIcon,mIvNext;
        //设备名字和设备状态信息
        TextView mTvDeviceName,mTvDeviceState;
    }
}
