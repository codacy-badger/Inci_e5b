package uo.asw.util;

import java.util.UUID;

public class UuidGenerator {
	
	public static String getUuid() {
		return UUID.randomUUID().toString().replace("-", "");
	}
}
