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