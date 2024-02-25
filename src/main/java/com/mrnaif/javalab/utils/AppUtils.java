package com.mrnaif.javalab.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.mrnaif.javalab.exception.InvalidRequestException;

public class AppUtils {

	private AppUtils() {
	}

	public static final void validatePageAndSize(Integer page, Integer size) {

		if (page < 1) {
			throw new InvalidRequestException("Page number cannot be less than one.");
		}

		if (size <= 0) {
			throw new InvalidRequestException("Size cannot be less than zero.");
		}

		if (size > AppConstant.MAX_PAGE_SIZE) {
			throw new InvalidRequestException("Page size must not be greater than " + AppConstant.MAX_PAGE_SIZE);
		}
	}

	public static byte[] getQRCodeImage(String text, int width, int height) throws WriterException, IOException {
		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

		ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
		MatrixToImageConfig config = new MatrixToImageConfig(0xFF000002, 0xFFFFD966);

		MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream, config);
		return pngOutputStream.toByteArray();
	}
}
