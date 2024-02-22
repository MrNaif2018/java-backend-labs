package com.mrnaif.javalab.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import com.mrnaif.javalab.model.QRCode;
import com.mrnaif.javalab.payload.QRCodeRequest;

@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@TestMethodOrder(OrderAnnotation.class)
@Rollback(false)
class QRCodeRepositoryTest {

	@Autowired
	QRCodeRepository codeRepository;

	@Autowired
	ModelMapper modelMapper;

	@SuppressWarnings("unlikely-arg-type")
	@Test
	@Order(1)
	void testCreateCode() {
		modelMapper.getConfiguration().setAmbiguityIgnored(true);

		QRCodeRequest codeRequest = new QRCodeRequest();

		codeRequest.setText("test");

		QRCode code = modelMapper.map(codeRequest, QRCode.class);

		codeRepository.save(code);

		assertThat(code.equals(codeRequest));
		assertThat(code.getId().equals(1));
	}

}
