package open.hui.ren.githubclientdemo.fragments.stars;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.agera.MutableRepository;
import com.google.android.agera.Result;

import java.io.IOException;
import java.util.ArrayList;

import javax.inject.Inject;

import open.hui.ren.githubclientdemo.BasePersistence;
import open.hui.ren.githubclientdemo.BasePresenter;
import open.hui.ren.githubclientdemo.BaseSupplier;
import open.hui.ren.githubclientdemo.ConstConfig;
import open.hui.ren.githubclientdemo.PreferenceService;
import open.hui.ren.githubclientdemo.apiservices.StarsAPIService;
import open.hui.ren.githubclientdemo.apiservices.params.StarsParams;
import open.hui.ren.githubclientdemo.entities.Repo;
import open.hui.ren.githubclientdemo.utils.ACache;
import retrofit2.Call;
import retrofit2.Retrofit;

/**
 * @author renhui
 * @date 16-10-25
 * @desc starred模块的业务模型，数据的获取、事件的推送、以及有关于数据存储的业务都由这里进行封装
 */

public class StarsSupplier extends BaseSupplier<ArrayList<Repo>> implements BasePersistence<ArrayList<Repo>> {

    @Inject
    ACache            mACache;
    @Inject
    Retrofit          mRetrofit;
    @Inject
    PreferenceService mPreferenceService;

    private MutableRepository<StarsParams> mSupplier;//上游数据supplier,主要负责参数输入

    private Context mContext;

    public StarsSupplier(BasePresenter presenter, MutableRepository supplier) {
        super(presenter, supplier);
        mPresenter = presenter;
        mSupplier = supplier;
        mContext = mPresenter.getView()
                             .getCtx();
        mPresenter.getView()
                  .getAppContext()
                  .getNetComponent()
                  .inject(this);
    }

    @NonNull
    @Override
    public Result<ArrayList<Repo>> get() {
        return loadData();
    }

    @Override
    public void saveData(ArrayList<Repo> data) {
        //TODO: sqlite support when necessary
    }

    @Override
    public void deleteData(ArrayList<Repo> data) {
        //TODO: sqlite support when necessary
    }

    @Override
    public ArrayList<Repo> retrieveData() {
        //TODO: sqlite support when necessary
        return null;
    }

    @Override
    public Result<ArrayList<Repo>> loadData() {
        StarsParams params = mSupplier.get();
        if (params == null) {
            //TODO: 此处关于参数无效的地方没有抛出throwable的对象有点不妥，后续需要自定义统一的throwable的标准
            return Result.failure();
        }
        Call<ArrayList<Repo>> call;
        call = mRetrofit.create(StarsAPIService.class)
                        .getUserStarred(params.userName);
        ArrayList<Repo> data;
        try {
            data = call.execute()
                       .body();
            mACache.put(ConstConfig.S_STARRED, data);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.failure(e);
        }
        return Result.success(data);
    }
}