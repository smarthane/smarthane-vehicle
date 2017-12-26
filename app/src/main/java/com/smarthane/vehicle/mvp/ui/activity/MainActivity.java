package com.smarthane.vehicle.mvp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;

import com.smarthane.vehicle.app.jni.JNISample;
import com.smarthane.vehicle.di.component.DaggerMainComponent;
import com.smarthane.vehicle.di.module.MainModule;
import com.smarthane.vehicle.mvp.contract.MainContract;
import com.smarthane.vehicle.mvp.presenter.MainPresenter;

import com.smarthane.vehicle.R;
import com.tbruyelle.rxpermissions2.RxPermissions;


import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.FlowableTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class MainActivity extends BaseActivity<MainPresenter> implements MainContract.View {


    @BindView(R.id.btn_test)
    Button btnTest;

    @BindView(R.id.tv_test)
    TextView tvTest;


    private RxPermissions mRxPermissions;

    @Override
    public void setupActivityComponent(AppComponent appComponent) {
        this.mRxPermissions = new RxPermissions(this);
        DaggerMainComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .mainModule(new MainModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(Bundle savedInstanceState) {
        return R.layout.activity_main; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mPresenter.requestPermissions();
        tvTest.setText(JNISample.getSample());
        if (true) {
            return;
        }

        String[] names = new String[]{"kobe", "james", "jodon"};

       // Observable.fromArray(names)

        Observable.fromArray(names).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(String s) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });


//        Observable.interval(1, TimeUnit.SECONDS)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<Long>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//
//            }
//
//            @Override
//            public void onNext(Long aLong) {
//                tvTest.setText(aLong+"");
//            }
//
//            @Override
//            public void onError(Throwable e) {
//
//            }
//
//            @Override
//            public void onComplete() {
//
//            }
//        });


        Observable.just("hello")
                .map(new Function<String, Integer>() {
                    @Override
                    public Integer apply(String s) throws Exception {
                        return s.length();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer s) throws Exception {
                        tvTest.setText(s+"");
                    }
                });

        List<String> f1 = new ArrayList<>();
        f1.add("kobe");
        f1.add("manu");
        f1.add("james");
        f1.add("jodon");

        Observable.fromArray(f1)
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<List<String>, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(List<String> strings) throws Exception {
                        return Observable.fromIterable(strings);
                    }
                })
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(String s) throws Exception {
                        if (s.equals("kobe"))
                        return true;
                        return false;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        System.out.print(s);
                        tvTest.setText(s);
                    }
                });

//        Observable.create(new ObservableOnSubscribe<String>() {
//                    @Override
//                    public void subscribe(ObservableEmitter<String> e) throws Exception {
//                        for (int i=0;i<5;i++) {
//                            Log.d("RXJAVA", i+"-emitter");
//                            e.onNext(""+i);
//                        }
//                    }
//                })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<String>() {
//                    @Override
//                    public void accept(String s) throws Exception {
//                        Log.d("RXJAVA", s+"-accept");
//                    }
//                });


        Flowable.create(new FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(FlowableEmitter<String> e) throws Exception {
                for (int i=0;i<180;i++) {
                    //Log.d("RXJAVA", i+"-emitter Flowable");
                    e.onNext(""+i);
                }
            }
        }, BackpressureStrategy.DROP)
                .compose(new FlowableTransformer() {
                    @Override
                    public Publisher apply(Flowable upstream) {
                        return upstream.subscribeOn(Schedulers.io())
                                .unsubscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread());
                    }
                })
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        subscription = s;
                        s.request(50);
                    }

                    @Override
                    public void onNext(String s) {
                        Log.d("RXJAVA", "onNext: " + s);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.w("RXJAVA", "onError: ", t);
                    }

                    @Override
                    public void onComplete() {
                        Log.w("RXJAVA", "onComplete: ");
                    }
                });





    }

    Subscription subscription;

    @OnClick({R.id.btn_test})
    public void onClick(View view) {
        subscription.request(50);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showMessage(@NonNull String message) {
        checkNotNull(message);
    }

    @Override
    public void launchActivity(@NonNull Intent intent) {
        checkNotNull(intent);
    }

    @Override
    public void killMyself() {
        finish();
    }

    @Override
    public RxPermissions getRxPermissions() {
        return mRxPermissions;
    }
}
