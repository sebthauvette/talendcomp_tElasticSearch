package de.jlo.talendcomp.elasticsearch;
import java.io.IOException;
import java.util.Date;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.junit.Test;

public class TestBulk extends Base {

	@Test
	public void testBulkImport() throws IOException {
		BulkRequestBuilder bulkRequest = getTransportClient().prepareBulk();

		// either use client#prepare, or use Requests# to directly build index/delete requests
		bulkRequest.add(getTransportClient().prepareIndex("twitter", "tweet", "1")
		        .setSource(XContentFactory.jsonBuilder()
		                    .startObject()
		                        .field("user", "kimchy")
		                        .field("postDate", new Date())
		                        .field("message", "trying out Elasticsearch")
		                    .endObject()
		                  )
		        );

		bulkRequest.add(getTransportClient().prepareIndex("twitter", "tweet", "2")
		        .setSource(XContentFactory.jsonBuilder()
		                    .startObject()
		                        .field("user", "kimchy")
		                        .field("postDate", new Date())
		                        .field("message", "another post")
		                    .endObject()
		                  )
		        );

		BulkResponse bulkResponse = bulkRequest.get();
		if (bulkResponse.hasFailures()) {
		    // process failures by iterating through each bulk response item
		}
	}
	
	public void testUpsert() throws Exception {
		IndexRequest indexRequest = new IndexRequest("index", "type", "1")
		        .source(XContentFactory.jsonBuilder()
		            .startObject()
		                .field("name", "Joe Smith")
		                .field("gender", "male")
		            .endObject());
		UpdateRequest updateRequest = new UpdateRequest("index", "type", "1")
		        .doc(XContentFactory.jsonBuilder()
		            .startObject()
		                .field("gender", "male")
		            .endObject())
		        .upsert(indexRequest);              
		getTransportClient().update(updateRequest).get();
	}
	
	public void testUpsertBulk() throws Exception {
		BulkRequestBuilder bulkRequest = getTransportClient().prepareBulk();

		IndexRequest indexRequest = new IndexRequest("index", "type", "1")
		        .source(XContentFactory.jsonBuilder()
		            .startObject()
		                .field("name", "Joe Smith")
		                .field("gender", "male")
		            .endObject());
		UpdateRequest updateRequest = new UpdateRequest("index", "type", "1")
		        .doc(XContentFactory.jsonBuilder()
		            .startObject()
		                .field("gender", "male")
		            .endObject())
		        .upsert(indexRequest);
		
		bulkRequest.add(updateRequest);
		
		bulkRequest.execute().actionGet(10000l);
	}

}
