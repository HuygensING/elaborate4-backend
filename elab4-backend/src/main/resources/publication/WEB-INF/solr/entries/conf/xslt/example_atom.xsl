<?xml version='1.0' encoding='UTF-8'?>
<!--
  #%L
  elab4-backend
  =======
  Copyright (C) 2011 - 2014 Huygens ING
  =======
  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as
  published by the Free Software Foundation, either version 3 of the
  License, or (at your option) any later version.
  
  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public
  License along with this program.  If not, see
  <http://www.gnu.org/licenses/gpl-3.0.html>.
  #L%
  -->


<!-- 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 -->

<!-- 
  Simple transform of Solr query results to Atom
 -->

<xsl:stylesheet version='1.0'
    xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>

  <xsl:output
       method="xml"
       encoding="utf-8"
       media-type="application/xml"
  />

  <xsl:template match='/'>
    <xsl:variable name="query" select="response/lst[@name='responseHeader']/lst[@name='params']/str[@name='q']"/>
    <feed xmlns="http://www.w3.org/2005/Atom">
      <title>Example Solr Atom 1.0 Feed</title>
      <subtitle>
       This has been formatted by the sample "example_atom.xsl" transform -
       use your own XSLT to get a nicer Atom feed.
      </subtitle>
      <author>
        <name>Apache Solr</name>
        <email>solr-user@lucene.apache.org</email>
      </author>
      <link rel="self" type="application/atom+xml" 
            href="http://localhost:8983/solr/q={$query}&amp;wt=xslt&amp;tr=atom.xsl"/>
      <updated>
        <xsl:value-of select="response/result/doc[position()=1]/date[@name='timestamp']"/>
      </updated>
      <id>tag:localhost,2007:example</id>
      <xsl:apply-templates select="response/result/doc"/>
    </feed>
  </xsl:template>
    
  <!-- search results xslt -->
  <xsl:template match="doc">
    <xsl:variable name="id" select="str[@name='id']"/>
    <entry>
      <title><xsl:value-of select="str[@name='name']"/></title>
      <link href="http://localhost:8983/solr/select?q={$id}"/>
      <id>tag:localhost,2007:<xsl:value-of select="$id"/></id>
      <summary><xsl:value-of select="arr[@name='features']"/></summary>
      <updated><xsl:value-of select="date[@name='timestamp']"/></updated>
    </entry>
  </xsl:template>

</xsl:stylesheet>
