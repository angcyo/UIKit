package com.angcyo.uiview.less.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.text.format.Formatter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.ArraySet;

import com.angcyo.lib.L;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.observables.SyncOnSubscribe;
import rx.schedulers.Schedulers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 应用程序缓存管理, 用来清理, 计算缓存大小
 * Created by angcyo on 2017/09/30 0030.
 */

public class RCacheManager {

    /**
     * 缓存目录列表
     */
    List<String> cachePaths = new ArrayList<>();
    Set<CacheInterceptor> interceptors = new ArraySet<>();

    private RCacheManager() {
    }

    public static RCacheManager instance() {
        return Holder.instance;
    }

    public void addCacheInterceptor(CacheInterceptor interceptor) {
        interceptors.add(interceptor);
    }

    public void removeCacheInterceptor(CacheInterceptor interceptor) {
        interceptors.remove(interceptor);
    }

    private static boolean clearPath(@NonNull String path, Set<CacheInterceptor> set) {
        if (set == null || set.isEmpty()) {
            return true;
        }
        boolean result = true;
        for (CacheInterceptor interceptor : set) {
            if (interceptor.interceptorClearPath(path)) {
                result = false;
                break;
            }
        }
        return result;
    }

    private static boolean clearFile(@NonNull File file, @NonNull String filePath, Set<CacheInterceptor> set) {
        if (set == null || set.isEmpty()) {
            return true;
        }
        boolean result = true;
        for (CacheInterceptor interceptor : set) {
            if (interceptor.interceptorClearFile(file, filePath)) {
                result = false;
                break;
            }
        }
        return result;
    }

    public static Observable<Boolean> clearCacheFolder(final String... paths) {
        return Observable
                .create(new SyncOnSubscribe<Integer, Boolean>() {
                    @Override
                    protected Integer generateState() {
                        return 1;
                    }

                    @Override
                    protected Integer next(Integer state, Observer<? super Boolean> observer) {
                        if (state > 0) {
                            for (String path : paths) {
                                deleteFolderFile(path, false, null);
                            }
                            observer.onNext(true);
                            observer.onCompleted();
                        }
                        return -1;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取指定文件夹内所有文件大小的和
     *
     * @param file file
     * @return size
     */
    public static long getFolderSize(File file) {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (File aFileList : fileList) {
                if (aFileList.isDirectory()) {
                    size = size + getFolderSize(aFileList);
                } else {
                    size = size + aFileList.length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 删除指定目录下的文件，这里用于缓存的删除
     *
     * @param filePath       filePath
     * @param deleteThisPath deleteThisPath
     */
    private static boolean deleteFolderFile(String filePath, boolean deleteThisPath, @Nullable Set<CacheInterceptor> set) {
        if (!TextUtils.isEmpty(filePath)) {
            File file = new File(filePath);
            return deleteFolderFile(file, deleteThisPath, set);
        }
        return false;
    }

    private static boolean deleteFolderFile(File file, boolean deleteThisPath, @Nullable Set<CacheInterceptor> set) {
        try {
            if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (File file1 : files) {
                    deleteFolderFile(file1.getAbsolutePath(), true, set);
                }
            }
            if (deleteThisPath) {
                if (!file.isDirectory()) {
                    if (clearFile(file, file.getAbsolutePath(), set)) {
                        file.delete();
                    }
                } else {
                    if (file.listFiles().length == 0) {
                        file.delete();
                    }
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取整个Cache文件夹大小
     */
    public long getAllCacheFolderSize() {
        try {
            long size = 0;
            for (String path : cachePaths) {
                size += getFolderSize(new File(path));
            }
            return size;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 打印log
     */
    public void log(Context context) {
        for (String path : cachePaths) {
            L.e(path + " -> " + Formatter.formatFileSize(context, getFolderSize(new File(path))));
        }
    }

    /**
     * 添加缓存目录, 需要用来统计大小的目录
     */
    public void addCachePath(String... paths) {
        for (String path : paths) {
            if (cachePaths.contains(path)) {
                continue;
            }
            File cache = new File(path);
            if (!cache.exists()) {
                cache.mkdirs();
            }
            cachePaths.add(path);
        }
    }

    /**
     * 添加SD根目录下的缓存目录
     */
    public void addSDCachePath(String... paths) {
        for (String path : paths) {
            addCachePath(Environment.getExternalStorageDirectory()
                    .getAbsoluteFile().getAbsolutePath() + File.separator + path);
        }
    }

    /**
     * 清理整个cache文件夹的文件
     */
    public Observable<Boolean> clearCacheFolder() {
        return Observable
                .create(new SyncOnSubscribe<Integer, Boolean>() {
                    @Override
                    protected Integer generateState() {
                        return 1;
                    }

                    @Override
                    protected Integer next(Integer state, Observer<? super Boolean> observer) {
                        if (state > 0) {
                            for (String path : cachePaths) {
                                if (clearPath(path, interceptors)) {
                                    deleteFolderFile(path, false, interceptors);
                                }
                            }
                            observer.onNext(true);
                            observer.onCompleted();
                        }
                        return -1;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private static class Holder {
        static RCacheManager instance = new RCacheManager();
    }

    public interface CacheInterceptor {
        /**
         * 是否需要拦截清理指定路径
         *
         * @return true 不清理此路径
         */
        boolean interceptorClearPath(@NonNull String path);

        /**
         * 是否需要拦截清理指定文件
         *
         * @return true 不清理此文件
         */
        boolean interceptorClearFile(@NonNull File file, @NonNull String filePath);
    }
}
