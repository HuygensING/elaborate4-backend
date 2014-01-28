package elaborate.editor.resources;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2014 Huygens ING
 * =======
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import nl.knaw.huygens.jaxrstools.resources.UTF8MediaType;
import nl.knaw.huygens.solr.RemoteSolrServer;
import nl.knaw.huygens.solr.SolrServerWrapper;

import com.google.common.collect.ImmutableMap;

import elaborate.editor.config.Configuration;
import elaborate.editor.model.orm.service.SearchService;
import elaborate.util.HibernateUtil;

@Path("status")
public class StatusResource extends AbstractElaborateResource {
	Configuration config = Configuration.instance();

	@GET
	@Produces(UTF8MediaType.APPLICATION_JSON)
	public Object getStatus() {
		return ImmutableMap.<String, Object> builder()//
				.put("solrserver", getSolrStatus())//
				.put("database", getDbStatus())//
				.build();
	}

	private ServerStatus getDbStatus() {
		EntityManager entityManager = HibernateUtil.getEntityManager();
		String url = (String) entityManager.getEntityManagerFactory().getProperties().get("hibernate.connection.url");
		boolean online = entityManager.isOpen();
		ServerStatus status = new ServerStatus(url, online);
		entityManager.close();
		return status;
	}

	private ServerStatus getSolrStatus() {
		SolrServerWrapper solrServer = SearchService.instance().getSolrServer();
		return new ServerStatus(((RemoteSolrServer) solrServer).getUrl(), solrServer.ping());
	}

	static class ServerStatus {
		String url;
		String status;

		public ServerStatus(String url, boolean online) {
			status = online ? "online" : "offline";
			this.url = url;
		}

		public String getUrl() {
			return url;
		}

		public String getStatus() {
			return status;
		}

	}
}
