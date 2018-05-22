package com.jaydenxiao.androidfire.ui.news.model;

import com.jaydenxiao.androidfire.api.Api;
import com.jaydenxiao.androidfire.api.ApiConstants;
import com.jaydenxiao.androidfire.api.HostType;
import com.jaydenxiao.androidfire.bean.NewsSummary;
import com.jaydenxiao.androidfire.ui.news.contract.NewsListContract;
import com.jaydenxiao.common.baserx.RxSchedulers;
import com.jaydenxiao.common.commonutils.TimeUtil;

import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * des:新闻列表model
 * Created by xsf
 * on 2016.09.14:54
 */
public class NewsListModel implements NewsListContract.Model {
    /**
     * 获取新闻列表
     * @param type
     * @param id
     * @param startPage
     * @return
     */
    @Override
    public Observable<List<NewsSummary>> getNewsListData(final String type, final String id, final int startPage) {
       return Api.getDefault(HostType.NETEASE_NEWS_VIDEO)
                .getNewsList(Api.getCacheControl(),type, id, startPage)
                //flatMap和map操作符很相像，有一个相同点都是把传入的参数转化之后返回另一个对象，不通点是flatMap发送的是合并后的Observables(要求是一对多的转化，比如一个学生类，可是类中每个学生都有很多不同的课程)
                .flatMap(new Func1<Map<String, List<NewsSummary>>, Observable<NewsSummary>>() {
                    @Override
                    public Observable<NewsSummary> call(Map<String, List<NewsSummary>> map) {
                        //以id作为主键是因为新闻类型的位置是可以拖动改变的
                        if (id.endsWith(ApiConstants.HOUSE_ID)) {
                            // 房产实际上针对地区的 它的id与返回key不同
                            return Observable.from(map.get("北京"));
                        }
                        return Observable.from(map.get(id));
                    }
                })
                //转化时间(.map()事件 对象的直接变换。所谓变换，就是将事件序列中的对象或整个序列进行加工处理，转换成不同的事件或事件序列。)（一对一）
                .map(new Func1<NewsSummary, NewsSummary>() {
                    @Override
                    public NewsSummary call(NewsSummary newsSummary) {
                        String ptime = TimeUtil.formatDate(newsSummary.getPtime());
                        newsSummary.setPtime(ptime);
                        return newsSummary;
                    }
                })
                .distinct()//去重，删除重复项
               //toSortList 类似于toList ,不同的 他能实现 对 列表进行排序 , 默认是自然升序, 数据项必须实现Comparable接口，才能排序
                .toSortedList(new Func2<NewsSummary, NewsSummary, Integer>() {
                    @Override
                    public Integer call(NewsSummary newsSummary, NewsSummary newsSummary2) {
                        return newsSummary2.getPtime().compareTo(newsSummary.getPtime());
                    }
                })
                //声明线程调度
                .compose(RxSchedulers.<List<NewsSummary>>io_main());
    }
}
