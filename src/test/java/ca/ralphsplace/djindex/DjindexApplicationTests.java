package ca.ralphsplace.djindex;

import ca.ralphsplace.djindex.controller.TradeDataController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DjindexApplicationTests {


	@Autowired
	TradeDataController controller;

	@Test
	void contextLoads() throws Exception {
		assertThat(controller).isNotNull();
	}
}
