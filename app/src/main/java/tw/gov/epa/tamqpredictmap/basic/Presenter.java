package tw.gov.epa.tamqpredictmap.basic;

/**
 * Created by user on 2017/6/26.
 */

public interface Presenter<V> {

    void dropView();

    void onLoad();

    void takeView(V view);
}