package com.service.content.platform;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import com.service.content.platform.model.Content;
import com.service.content.platform.repository.ContentRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertTrue;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles({ "dev" })
public final class ContentLogicTests {

	@Autowired
	private AmazonDynamoDB amazonDynamoDB;

	@Autowired
	private ContentRepository contentRepository;

	private static boolean taskTableExist = false;

	@Before
	public void setUp() throws TableUtils.TableNeverTransitionedToStateException, InterruptedException {
		if( ! taskTableExist ) {
			if(TableUtils.createTableIfNotExists(amazonDynamoDB, new DynamoDBMapper(amazonDynamoDB).generateCreateTableRequest(Content.class)
					.withProvisionedThroughput(new ProvisionedThroughput(2L, 2L)))) {
				log.info("~~~ Waiting for Contents table to be active ~~~");
				TableUtils.waitUntilActive(amazonDynamoDB, "contents-table");
			}
			else {
				log.info("~~~ Contents table already exists ~~~");
			}
			taskTableExist = true;
		}
	}

	@Test
	public void contextLoads() {}

	@Test
	public void testSaveAndRemoveContent() {
		log.info("Saving content");

		Content content = new Content().setId("TEST-ID1").setName("Test Content 1");

		Content savedContent = contentRepository.save(content);

		assertTrue("Content saved successfully.", savedContent != null);
		assertTrue("Content Id matches", (savedContent != null && savedContent.getId().compareTo("TEST-ID1") == 0));
		log.info("Save content done");

		log.info("Removing content with ID = TEST-ID1 ");
		contentRepository.deleteById("TEST-ID1");

		assertTrue("Content removed successfully.", !contentRepository.findById("TEST-ID1").isPresent());
		log.info("remove content done");

	}

}
