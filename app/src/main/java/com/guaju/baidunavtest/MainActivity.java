package com.guaju.baidunavtest;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BaiduNaviManagerFactory;
import com.baidu.navisdk.adapter.IBNRoutePlanManager;
import com.baidu.navisdk.adapter.IBNTTSManager;
import com.baidu.navisdk.adapter.IBaiduNaviManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BNRoutePlanNode mStartNode = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initBaiduNav();
        initTTS();


    }

    private void getRoutesAndNav() {
        int coType=0;
        BNRoutePlanNode sNode = new BNRoutePlanNode(116.30142, 40.05087, "百度大厦", "百度大厦", coType);
        BNRoutePlanNode eNode = new BNRoutePlanNode(116.39750, 39.90882, "北京天安门", "北京天安门", coType);
        switch (coType) {
            case BNRoutePlanNode.CoordinateType.GCJ02: {
                sNode = new BNRoutePlanNode(116.30142, 40.05087, "百度大厦", "百度大厦", coType);
                eNode = new BNRoutePlanNode(116.39750, 39.90882, "北京天安门", "北京天安门", coType);
                break;
            }
            case BNRoutePlanNode.CoordinateType.WGS84: {
                sNode = new BNRoutePlanNode(116.300821, 40.050969, "百度大厦", "百度大厦", coType);
                eNode = new BNRoutePlanNode(116.397491, 39.908749, "北京天安门", "北京天安门", coType);
                break;
            }
            case BNRoutePlanNode.CoordinateType.BD09_MC: {
                sNode = new BNRoutePlanNode(12947471, 4846474, "百度大厦", "百度大厦", coType);
                eNode = new BNRoutePlanNode(12958160, 4825947, "北京天安门", "北京天安门", coType);
                break;
            }
            case BNRoutePlanNode.CoordinateType.BD09LL: {
                sNode = new BNRoutePlanNode(116.30784537597782, 40.057009624099436, "百度大厦", "百度大厦", coType);
                eNode = new BNRoutePlanNode(116.40386525193937, 39.915160800132085, "北京天安门", "北京天安门", coType);
                break;
            }
            default:
                ;
        }
        mStartNode = sNode;

        List<BNRoutePlanNode> list = new ArrayList<BNRoutePlanNode>();
        list.add(sNode);
        list.add(eNode);

        BaiduNaviManagerFactory.getRoutePlanManager().routeplanToNavi(
                list,
                IBNRoutePlanManager.RoutePlanPreference.ROUTE_PLAN_PREFERENCE_DEFAULT,
                null,
                new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        switch (msg.what) {
                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_START:
                                Toast.makeText(MainActivity.this, "算路开始", Toast.LENGTH_SHORT)
                                        .show();
                                break;
                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_SUCCESS:
                                Toast.makeText(MainActivity.this, "算路成功", Toast.LENGTH_SHORT)
                                        .show();
                                break;
                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_FAILED:
                                Toast.makeText(MainActivity.this, "算路失败", Toast.LENGTH_SHORT)
                                        .show();
                                break;
                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_TO_NAVI:
                                Toast.makeText(MainActivity.this, "算路成功准备进入导航", Toast.LENGTH_SHORT)
                                        .show();
                                Intent intent = new Intent(MainActivity.this,
                                        DemoGuideActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("route", mStartNode);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                break;
                            default:
                                // nothing
                                break;
                        }
                    }
                });

    }

    //tts注册
    private void initTTS() {
        BaiduNaviManagerFactory.getTTSManager().initTTS(this, Environment.getExternalStorageDirectory()+"", "baidu_nav","11425466");

        // 注册同步内置tts状态回调
        BaiduNaviManagerFactory.getTTSManager().setOnTTSStateChangedListener(
                new IBNTTSManager.IOnTTSPlayStateChangedListener() {
                    @Override
                    public void onPlayStart() {
                        //语音开始播报
                        Log.e("BNSDKDemo", "小明开始播放语音了.onPlayStart");
                    }

                    @Override
                    public void onPlayEnd(String speechId) {
                        //语音播报完成
                        Log.e("BNSDKDemo", "小明播放语音完毕哦.onPlayEnd");
                    }

                    @Override
                    public void onPlayError(int code, String message) {
                        //语音播放失败
                        Log.e("BNSDKDemo", "小明播放语音失败了.onPlayError");
                    }
                }
        );

        // 注册内置tts 异步状态消息
        BaiduNaviManagerFactory.getTTSManager().setOnTTSStateChangedHandler(
                new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        Log.e("BNSDKDemo", "ttsHandler.msg.what=" + msg.what);
                    }
                }
        );
    }

    private void initBaiduNav() {
        BaiduNaviManagerFactory.getBaiduNaviManager().init(this, Environment.getExternalStorageDirectory()+"", "baidu_nav",
                new IBaiduNaviManager.INaviInitListener() {

                    private String authinfo;

                    @Override
                    public void onAuthResult(int status, String msg) {
                        //认证百度导航的结果
                        if (0 == status) {
                            authinfo = "key校验成功!";
                        } else {
                            authinfo = "key校验失败, " + msg;
                        }
                        MainActivity.this.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, authinfo, Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override
                    public void initStart() {
                      //初始化百度导航sdk开始
                        Toast.makeText(MainActivity.this, "开始打开认证", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void initSuccess() {
                        //初始化百度导航sdk成功
                        Toast.makeText(MainActivity.this, "打开认证成功", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void initFailed() {
                        //初始化百度导航sdk失败
                        Toast.makeText(MainActivity.this, "打开认证失败", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public void startRoute(View view) {
        //规划路线 并且开始导航
        getRoutesAndNav();
    }
}
