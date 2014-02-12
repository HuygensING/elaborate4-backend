###
# #%L
# elab4-backend
# =======
# Copyright (C) 2011 - 2014 Huygens ING
# =======
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as
# published by the Free Software Foundation, either version 3 of the
# License, or (at your option) any later version.
# 
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public
# License along with this program.  If not, see
# <http://www.gnu.org/licenses/gpl-3.0.html>.
# #L%
###
#!/bin/env ruby

require 'pp'
require 'rest_client'
require 'json'

#@baseurl = "http://rest.elaborate.huygens.knaw.nl"
@baseurl ="http://10.152.32.62:2013"
@elabbe = RestClient::Resource.new(@baseurl)
@users = RestClient::Resource.new(@baseurl+"/users")
@sessions = RestClient::Resource.new(@baseurl+"/sessions")
@projects = RestClient::Resource.new(@baseurl+"/projects")

def main
  response = RestClient.get(@baseurl+"version")
  pp response.headers
  pp response
end

def login
  raw=@sessions["/login"].post(:username =>"root", :password=>"toor")
  cooked = JSON.parse(raw)
  RestClient.add_before_execution_proc do |req, params|
    req.add_field('Authorization',"SimpleAuth #{cooked['token']}")
  end
end

def projects
  JSON.parse(@projects.get)
end

def users
  JSON.parse(@users.get)
end

login
pp projects
pp users
