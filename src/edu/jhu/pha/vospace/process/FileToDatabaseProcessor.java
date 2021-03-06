/*******************************************************************************
 * Copyright 2013 Johns Hopkins University
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package edu.jhu.pha.vospace.process;

import java.util.ArrayList;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MediaTypeRegistry;
import org.apache.tika.mime.MimeTypes;
import org.codehaus.jackson.JsonNode;

import edu.jhu.pha.vospace.node.NodeType;
import edu.jhu.pha.vospace.process.database.Database;
import edu.jhu.pha.vospace.process.database.MyDB;
import edu.jhu.pha.vospace.process.sax.AsciiTable;
import edu.jhu.pha.vospace.process.sax.AsciiTableContentHandler;

public class FileToDatabaseProcessor extends Processor {

	private static final MediaTypeRegistry MIME_REGISTRY = new MimeTypes().getMediaTypeRegistry();
	private static final MediaType CSV_TYPE = MediaType.text("csv");
	protected FileToDatabaseProcessor(ProcessorConfig config) {
		super(config);
	}
	
	@Override
	public void processNodeMeta(Metadata metadata, JsonNode credentials) throws ProcessingException {
		Database db = new MyDB(credentials);
		db.setup();
		db.update(metadata, (ArrayList<AsciiTable>)((AsciiTableContentHandler)handler).getTables());
		
		if(!metadata.get(Metadata.CONTENT_TYPE).equals(CSV_TYPE.toString()) &&
				MIME_REGISTRY.isSpecializationOf(CSV_TYPE, MediaType.parse(metadata.get(Metadata.CONTENT_TYPE)))){
			metadata.set(Metadata.CONTENT_TYPE, CSV_TYPE.toString());
		}		

		metadata.set("NodeType", NodeType.STRUCTURED_DATA_NODE.toString());
  	}
}
