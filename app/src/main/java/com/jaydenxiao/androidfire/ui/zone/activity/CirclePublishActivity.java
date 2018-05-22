package com.jaydenxiao.androidfire.ui.zone.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.jaydenxiao.androidfire.R;
import com.jaydenxiao.androidfire.app.AppApplication;
import com.jaydenxiao.androidfire.app.AppConstant;
import com.jaydenxiao.androidfire.ui.zone.DatasUtil;
import com.jaydenxiao.androidfire.ui.zone.adapter.NinePicturesAdapter;
import com.jaydenxiao.androidfire.ui.zone.bean.CircleItem;
import com.jaydenxiao.androidfire.ui.zone.contract.PublishContract;
import com.jaydenxiao.androidfire.ui.zone.model.PublishModel;
import com.jaydenxiao.androidfire.ui.zone.presenter.PublishPresenter;
import com.jaydenxiao.common.base.BaseActivity;
import com.jaydenxiao.common.baseapp.AppCache;
import com.jaydenxiao.common.commonutils.ACache;
import com.jaydenxiao.common.commonutils.ImageLoaderUtils;
import com.jaydenxiao.common.commonutils.LocalizeUtil;
import com.jaydenxiao.common.commonutils.LogUtils;
import com.jaydenxiao.common.commonutils.ToastUtil;
import com.jaydenxiao.common.commonwidget.NoScrollGridView;
import com.jaydenxiao.common.commonwidget.NormalTitleBar;
import com.yuyh.library.imgsel.ImageLoader;
import com.yuyh.library.imgsel.ImgSelActivity;
import com.yuyh.library.imgsel.ImgSelConfig;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * des:发布说说
 * Created by xsf
 * on 2016.09.11:49
 */
public class CirclePublishActivity extends BaseActivity<PublishPresenter,PublishModel> implements PublishContract.View, View.OnClickListener {
    @Bind(R.id.ntb)
    NormalTitleBar ntb;
    @Bind(R.id.et_content)
    EditText etContent;
    @Bind(R.id.gridview)
    NoScrollGridView gridview;
    private NinePicturesAdapter ninePicturesAdapter;
    private int REQUEST_CODE=120;

    CircleItem circleItem = new CircleItem();

    /**
     * 启动入口
     * @param context
     */
    public static void startAction(Context context) {
        Intent intent = new Intent(context, CirclePublishActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.act_publish_zone;
    }

    @Override
    public void initPresenter() {
        mPresenter.setVM(this,mModel);

    }

    @Override
    public void initView() {
        ntb.setTitleText(getString(R.string.zone_publish_title));
        ninePicturesAdapter = new NinePicturesAdapter(this,9, new NinePicturesAdapter.OnClickAddListener() {
            @Override
            public void onClickAdd(int positin) {
                choosePhoto();
            }
        });
        gridview.setAdapter(ninePicturesAdapter);
    }

    @OnClick({R.id.tv_back,R.id.tv_save})
    public void onClick(View view) {
      switch (view.getId()){
          case R.id.tv_back:
              finish();
          break;
          //提交
          case R.id.tv_save:
              if(!TextUtils.isEmpty(etContent.getText().toString())) {
                  circleItem.setContent(etContent.getText().toString());
                  circleItem.setPictures(getPictureString());
                  circleItem.setIcon(AppCache.getInstance().getIcon());
                  circleItem.setUserId(AppCache.getInstance().getUserId());
                  circleItem.setNickName(AppCache.getInstance().getUserName());
//                  circleItem.setCreateTime(Long.parseLong("1471942968000"));//设置时间为2016-08-23

                  //获取当前时间进行设置，Date与Calendar二选一
                  Date nowtime = new Date();
                  Calendar c = Calendar.getInstance();

                  LogUtils.loge("nowtime========="+ c.getTimeInMillis());
                  circleItem.setCreateTime(Long.parseLong(String.valueOf(c.getTimeInMillis())));
//                  circleItem.setCreateTime(nowtime.getTime());

                  //有服务器的话把先把发布地址发给服务器，UI界面从服务器获取发布地址
                  ACache.get(AppApplication.getAppContext()).put(AppConstant.ZONE_PUBLISH_ADD,DatasUtil.getPublishAddresss());
                  circleItem.setAddress(ACache.get(AppApplication.getAppContext()).getAsString(AppConstant.ZONE_PUBLISH_ADD));
//                  mPresenter.getPublishAddressRequest();//获取发布说说时所在的位置

                  mRxManager.post(AppConstant.ZONE_PUBLISH_ADD,circleItem);//新增说说
                  finish();
              }else{
                  ToastUtil.showToastWithImg(getString(R.string.circle_publish_empty),R.drawable.ic_warm);
              }
              break;
      }
    }

    /**
     * 开启图片选择器
     */
    private void choosePhoto() {
        ImgSelConfig config = new ImgSelConfig.Builder(loader)
                // 是否多选
                .multiSelect(true)
                // 确定按钮背景色
                .btnBgColor(Color.TRANSPARENT)
                .titleBgColor(ContextCompat.getColor(this,R.color.main_color))
                // 使用沉浸式状态栏
                .statusBarColor(ContextCompat.getColor(this,R.color.main_color))
                // 返回图标ResId
                .backResId(R.drawable.ic_arrow_back)
                .title("图片")
                // 第一个是否显示相机
                .needCamera(true)
                // 最大选择图片数量
                .maxNum(9-ninePicturesAdapter.getPhotoCount())
                .build();
        ImgSelActivity.startActivity(this, config, REQUEST_CODE);
    }
    private ImageLoader loader = new ImageLoader() {
        @Override
        public void displayImage(Context context, String path, ImageView imageView) {
            ImageLoaderUtils.display(context,imageView,path);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            List<String> pathList = data.getStringArrayListExtra(ImgSelActivity.INTENT_RESULT);
            if(ninePicturesAdapter!=null){
                ninePicturesAdapter.addAll(pathList);
            }
        }
    }

    /**
     * 获取到拼接好的图片
     * @return
     */
    private String getPictureString(){
        //拼接图片链接
        List<String> strings = ninePicturesAdapter.getData();
        if (strings != null && strings.size() > 0) {
            StringBuilder allUrl = new StringBuilder();
            for (int i = 0; i < strings.size(); i++) {
                if (!TextUtils.isEmpty(strings.get(i))) {
                    allUrl.append(strings.get(i) + ";");
                }
            }
            if (!TextUtils.isEmpty(allUrl)) {
                String url = allUrl.toString();
                url = url.substring(0, url.lastIndexOf(";"));
                return url;
            }else{
                return "";
            }
        }else{
            return "";
        }
    }

    //返回发布地址添加进CircleItem
    @Override
    public void returnPublishAddress(List<CircleItem> circleItemList) {
        LogUtils.loge("ruiwenwen++"+circleItemList.get(0).getAddress());
        if (circleItemList != null) {
            circleItem.setAddress(circleItemList.get(0).getAddress());
        }

    }

    @Override
    public void showLoading(String title) {

    }

    @Override
    public void stopLoading() {

    }

    @Override
    public void showErrorTip(String msg) {

    }

    @Override
    protected void onDestroy() {
        LocalizeUtil.unregister();
        super.onDestroy();
    }
}
