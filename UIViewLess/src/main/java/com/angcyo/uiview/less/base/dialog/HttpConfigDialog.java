package com.angcyo.uiview.less.base.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import com.angcyo.http.Http;
import com.angcyo.http.Json;
import com.angcyo.lib.L;
import com.angcyo.uiview.less.R;
import com.angcyo.uiview.less.recycler.RBaseViewHolder;
import com.angcyo.uiview.less.utils.RDialog;
import com.angcyo.uiview.less.utils.T_;
import com.angcyo.uiview.less.widget.OnSpinnerItemSelected;
import com.angcyo.uiview.less.widget.RSpinner;
import com.angcyo.uiview.less.widget.RSpinnerAdapter;
import retrofit2.RetrofitServiceMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Email:angcyo@126.com
 *
 * @author angcyo
 * @date 2018/12/13
 */
public class HttpConfigDialog {
    public static String MAPPING_URL = "https://www.angcyo.com/api/php/android/c/url_mapping";

    public static void show(@NonNull final Context context, final String baseUrl, @Nullable final OnHttpConfig onHttpConfig) {
        show(context, baseUrl, null, onHttpConfig);
    }

    /**
     * @param urlList 可以使用空格, key:value 的形式, 会取空格分隔后的最后一个
     */
    public static void show(@NonNull final Context context, final String baseUrl, final List<String> urlList, @Nullable final OnHttpConfig onHttpConfig) {
        RDialog.build(context)
                .setCanceledOnTouchOutside(false)
                .setContentLayoutId(R.layout.base_http_config_layout)
                .setInitListener(new RDialog.OnInitListener() {
                    @Override
                    public void onInitDialog(@NonNull final Dialog dialog, @NonNull final RBaseViewHolder dialogViewHolder) {
                        dialogViewHolder.exV(R.id.host_edit).setInputText(baseUrl);

                        dialogViewHolder.cb(R.id.map_box, RetrofitServiceMapping.enableMapping, new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                dialogViewHolder.v(R.id.get_list).setEnabled(isChecked);

                                RetrofitServiceMapping.init(isChecked, RetrofitServiceMapping.defaultMap);
                            }
                        });

                        dialogViewHolder.click(R.id.get_list, new View.OnClickListener() {
                            @Override
                            public void onClick(final View v) {
                                Http.request(MAPPING_URL, new Http.OnHttpRequestCallback() {
                                    @Override
                                    public void onRequestCallback(@NonNull final String body) {
                                        L.json(body);
                                        Map mapping = Json.from(body, Map.class);
                                        RetrofitServiceMapping.init(RetrofitServiceMapping.enableMapping, mapping);

                                        v.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                T_.info(body);
                                            }
                                        });
                                    }
                                });
                            }
                        });

                        RSpinner spinner = dialogViewHolder.v(R.id.url_spinner);
                        List<String> urls = new ArrayList<>();
                        urls.add("选择服务器");
                        if (urlList == null || urlList.isEmpty()) {
                            urls.add(baseUrl);
                        } else {
                            urls.addAll(urlList);
                        }

                        spinner.setAdapter(new RSpinnerAdapter<String>(context, urls) {
                            @Override
                            public void onBindDropDownItemView(@NonNull RBaseViewHolder itemViewHolder, int position, String itemBean) {
                                super.onBindDropDownItemView(itemViewHolder, position, itemBean);
                                itemViewHolder.tv(R.id.base_text_view).setText(itemBean);
                            }
                        });

                        final List<String> finalUrls = urls;
                        spinner.setOnItemSelectedListener(new OnSpinnerItemSelected() {
                            @Override
                            public void onItemSelected(@Nullable AdapterView<?> parent, @Nullable View view, int position, long id) {
                                super.onItemSelected(parent, view, position, id);
                                if (position == 0) {
                                    return;
                                }
                                String url = finalUrls.get(position);
                                String[] split = url.split(" ");
                                dialogViewHolder.exV(R.id.host_edit).setInputText(split[split.length - 1]);
                            }
                        });

                        dialogViewHolder.click(R.id.save_button, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (onHttpConfig != null) {
                                    onHttpConfig.onSaveBaseUrl(dialogViewHolder.exV(R.id.host_edit).string());
                                }

                                dialog.cancel();
                            }
                        });
                    }
                })
                .showAlertDialog();
    }

    public interface OnHttpConfig {
        void onSaveBaseUrl(@NonNull String newBaseUrl);
    }
}
