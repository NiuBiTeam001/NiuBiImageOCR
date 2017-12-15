package com.jeve.cr.tool;

import android.util.Log;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.sdk.model.BankCardParams;
import com.baidu.ocr.sdk.model.BankCardResult;
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

    private OCRTool() {}

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

    /**
     * 初始化
     */
    public void init() {
        OCR.getInstance().initAccessToken(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken accessToken) {
                token = accessToken.getAccessToken();
            }

            @Override
            public void onError(OCRError ocrError) {

            }
        }, CrApplication.getContext());
    }

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

    //银行卡识别
    public void OCRBankCard(String filePath, final OcrBankCallBack ocrBankCallBack) {
        // 银行卡识别参数设置
        BankCardParams param = new BankCardParams();
        param.setImageFile(new File(filePath));

        // 调用银行卡识别服务
        OCR.getInstance().recognizeBankCard(param, new OnResultListener<BankCardResult>() {
            @Override
            public void onResult(BankCardResult result) {
                // 调用成功，返回BankCardResult对象
                ocrBankCallBack.success(result.getBankCardNumber(), result.getBankName());
            }

            @Override
            public void onError(OCRError error) {
                // 调用失败，返回OCRError对象
                ocrBankCallBack.error(error.toString());
            }
        });
    }

    public interface OcrCallBack {
        //识别成功
        void success(String str);

        //识别失败
        void error(String error);
    }

    public interface OcrBankCallBack {
        //识别成功
        void success(String carNum, String bankName);

        //识别失败
        void error(String error);
    }

}
