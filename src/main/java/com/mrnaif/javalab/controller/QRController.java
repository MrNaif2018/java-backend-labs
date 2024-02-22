package com.mrnaif.javalab.controller;

import java.io.IOException;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.zxing.WriterException;
import com.mrnaif.javalab.payload.PageResponse;
import com.mrnaif.javalab.payload.QRCodeResponse;
import com.mrnaif.javalab.service.QRCodeService;
import com.mrnaif.javalab.service.StorageService;
import com.mrnaif.javalab.utils.AppConstant;
import com.mrnaif.javalab.utils.AppUtils;

@RestController
@RequestMapping("/api/v1/qr")
public class QRController {

	QRCodeService codeService;

	StorageService storageService;

	public QRController(QRCodeService codeService, StorageService storageService) {
		this.codeService = codeService;
		this.storageService = storageService;
	}

	@PostMapping
	public ResponseEntity<QRCodeResponse> createCode(@RequestParam("text") String text) {
		byte[] array;
		try {
			array = AppUtils.getQRCodeImage(text, 500, 500);
		} catch (WriterException | IOException e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		QRCodeResponse response = codeService.createCode(text, array);
		return new ResponseEntity<>(response, HttpStatus.CREATED);

	}

	@GetMapping
	public ResponseEntity<PageResponse<QRCodeResponse>> getAllCodes(
			@RequestParam(value = "page", required = false, defaultValue = AppConstant.DEFAULT_PAGE_NUMBER) Integer page,
			@RequestParam(value = "size", required = false, defaultValue = AppConstant.DEFAULT_PAGE_SIZE) Integer size) {
		PageResponse<QRCodeResponse> codeResponses = codeService.getAllCodes(page, size);
		return new ResponseEntity<>(codeResponses, HttpStatus.OK);
	}

	@GetMapping("/{code_id}")
	public ResponseEntity<ByteArrayResource> getCodeById(
			@PathVariable("code_id") Long codeId) {
		QRCodeResponse codeResponse = codeService.getCodeById(codeId);
		byte[] data = storageService.load(codeResponse.getPath());
		ByteArrayResource resource = new ByteArrayResource(data);
		return ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.contentLength(resource.contentLength())
				.header(HttpHeaders.CONTENT_DISPOSITION,
						ContentDisposition.attachment()
								.filename(codeResponse.getId() + ".png")
								.build().toString())
				.body(resource);
	}
}
