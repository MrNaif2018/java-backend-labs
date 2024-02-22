package com.mrnaif.javalab.payload;

import lombok.Data;

@Data
public class QRCodeResponse {

	private Long id;

	private String text;

	private String path;

	private String created;

}
