package tw.gov.epa.tamqpredictmap.basic;

/**
 * Created by user on 2017/6/26.
 */

public abstract class BasePresenter<T> implements Presenter<T> {

    protected T view;

    @Override
    public void dropView() {
        this.view = null;
    }

    @Override
    public void onLoad() {
        // template method
    }

    @Override
    public void takeView(T view) {
        this.view = view;
        onLoad();
    }

    protected boolean hasView() {
        return view != null;
    }
}