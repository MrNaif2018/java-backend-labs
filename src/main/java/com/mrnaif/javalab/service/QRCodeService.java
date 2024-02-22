package com.mrnaif.javalab.service;

import com.mrnaif.javalab.payload.PageResponse;
import com.mrnaif.javalab.payload.QRCodeResponse;

public interface QRCodeService {

	QRCodeResponse createCode(String text, byte[] data);

	PageResponse<QRCodeResponse> getAllCodes(Integer page, Integer size);

	QRCodeResponse getCodeById(Long codeId);

}
