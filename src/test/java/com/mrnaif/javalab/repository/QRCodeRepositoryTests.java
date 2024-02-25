package com.mrnaif.javalab.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.mrnaif.javalab.model.QRCode;
import com.mrnaif.javalab.payload.QRCodeRequest;

@SpringBootTest
class QRCodeRepositoryTests {

	@Autowired
	QRCodeRepository codeRepository;

	@Autowired
	ModelMapper modelMapper;

	@Test
	@Order(1)
	void testCreateCode() {
		QRCodeRequest codeRequest = new QRCodeRequest();

		codeRequest.setText("test");

		QRCode code = modelMapper.map(codeRequest, QRCode.class);

		codeRepository.save(code);

		assertThat(code.getText()).isEqualTo(codeRequest.getText());
		assertThat(code.getId()).isNotNull();
	}

}
