package com.mrnaif.javalab.service.impl;

import java.util.Arrays;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mrnaif.javalab.exception.ResourceNotFoundException;
import com.mrnaif.javalab.model.QRCode;
import com.mrnaif.javalab.payload.PageResponse;
import com.mrnaif.javalab.payload.QRCodeResponse;
import com.mrnaif.javalab.repository.QRCodeRepository;
import com.mrnaif.javalab.service.QRCodeService;
import com.mrnaif.javalab.service.StorageService;
import com.mrnaif.javalab.utils.AppConstant;
import com.mrnaif.javalab.utils.AppUtils;

@Service
@Transactional
public class QRCodeServiceImpl implements QRCodeService {

	QRCodeRepository codeRepository;

	StorageService storageService;

	ModelMapper modelMapper;

	public QRCodeServiceImpl(QRCodeRepository codeRepository, StorageService storageService, ModelMapper modelMapper) {
		this.codeRepository = codeRepository;
		this.storageService = storageService;
		this.modelMapper = modelMapper;
	}

	@Override
	public QRCodeResponse createCode(String text, byte[] data) {
		QRCode code = new QRCode();
		code.setText(text);
		codeRepository.save(code);
		String path = storageService.store(code.getId() + ".png", data);
		code.setPath(path);
		codeRepository.save(code);
		return modelMapper.map(code, QRCodeResponse.class);
	}

	@Override
	public PageResponse<QRCodeResponse> getAllCodes(Integer page, Integer size) {

		AppUtils.validatePageAndSize(page, size);
		Pageable pageable = PageRequest.of(page - 1, size);
		Page<QRCode> codes = codeRepository.findAll(pageable);
		List<QRCodeResponse> codeResponses = Arrays.asList(modelMapper.map(codes.getContent(), QRCodeResponse[].class));

		PageResponse<QRCodeResponse> pageResponse = new PageResponse<>();
		pageResponse.setContent(codeResponses);
		pageResponse.setSize(size);
		pageResponse.setPage(page);
		pageResponse.setTotalElements(codes.getNumberOfElements());
		pageResponse.setTotalPages(codes.getTotalPages());
		pageResponse.setLast(codes.isLast());

		return pageResponse;
	}

	@Override
	public QRCodeResponse getCodeById(Long codeId) {

		QRCode code = codeRepository.findById(codeId)
				.orElseThrow(() -> new ResourceNotFoundException(AppConstant.QRCODE_NOT_FOUND + codeId));
		return modelMapper.map(code, QRCodeResponse.class);
	}

}
