package com.jeve.cr.tool;

import android.util.Log;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.sdk.model.GeneralBasicParams;
import com.baidu.ocr.sdk.model.GeneralResult;
import com.baidu.ocr.sdk.model.WordSimple;
import com.jeve.cr.CrApplication;

import java.io.File;

/**
 * 图像识别类
 * lijiawei
 * 2017-12-6
 */
public class OCRTool {

    private OCRTool() {
        OCR.getInstance().initAccessToken(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken accessToken) {
                token = accessToken.getAccessToken();
                Log.d("LJW", "token = " + token);
            }

            @Override
            public void onError(OCRError ocrError) {

            }
        }, CrApplication.getContext());
    }

    private static OCRTool ocrTool;

    public static OCRTool getInstence() {
        if (ocrTool == null) {
            synchronized (OCRTool.class) {
                if (ocrTool == null) {
                    ocrTool = new OCRTool();
                }
            }
        }
        return ocrTool;
    }

    private String token;

    //识别文字
    public void OCRTest(String imagePath, final OcrCallBack ocrCallBack) {
        final StringBuffer stb = new StringBuffer();
        GeneralBasicParams params = new GeneralBasicParams();
        params.setDetectDirection(true);
        params.setImageFile(new File(imagePath));
        OCR.getInstance().recognizeGeneralBasic(params, new OnResultListener<GeneralResult>() {
            @Override
            public void onResult(GeneralResult generalResult) {
                for (WordSimple w : generalResult.getWordList()) {
                    stb.append(w.getWords());
                    stb.append("\n");
                }
                ocrCallBack.success(stb.toString());
            }

            @Override
            public void onError(OCRError ocrError) {
                ocrCallBack.error(ocrError.toString());
            }
        });
    }

    public interface OcrCallBack {
        //识别成功
        void success(String str);

        //识别失败
        void error(String error);
    }

}
