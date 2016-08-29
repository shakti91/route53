package com.shakti.route53;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.services.route53.model.HostedZone;
import com.amazonaws.services.route53.model.ResourceRecordSet;

@RestController
public class Route53Controller {
	
	@Autowired
	Route53Service route53Service;
	
	@RequestMapping(value="hostedZones", method=RequestMethod.GET)
	public List<String> listHostedZones() {
		List<HostedZone> hostedZones = route53Service.listHostedZones();
		List<String> hostedZoneNames = new ArrayList<>();
		
		for(HostedZone hostedZone : hostedZones) {
			hostedZoneNames.add(hostedZone.getName());
		}
		
		return hostedZoneNames;
	}
	
	@RequestMapping(value="hostedZones/listAll", method=RequestMethod.GET)
	public List<String> listAllHostedZones() {
		List<HostedZone> hostedZones = route53Service.listAllHostedZones();
		List<String> hostedZoneNames = new ArrayList<>();
		
		for(HostedZone hostedZone : hostedZones) {
			hostedZoneNames.add(hostedZone.getName());
		}
		
		return hostedZoneNames;
	}
	
	@RequestMapping(value="hostedZones/by_name", method=RequestMethod.GET)
	public List<String> listHostedZonesByName() {
		List<HostedZone> hostedZones = route53Service.listHostedZonesByName();
		List<String> hostedZoneNames = new ArrayList<>();
		
		for(HostedZone hostedZone : hostedZones) {
			hostedZoneNames.add(hostedZone.getName());
		}
		
		return hostedZoneNames;
	}
	
	@RequestMapping(value="hostedZones/{id}", method=RequestMethod.GET)
	public HostedZone getHostedZoneById(@PathVariable String id) {
		HostedZone hostedZone = route53Service.getHostedZoneById(id);		
		return hostedZone;
	}
	
	@RequestMapping(value="hostedZones/unique_by_name", method=RequestMethod.GET)
	public HostedZone getHostedZoneByName(@RequestParam String domainName) {
		HostedZone hostedZone = route53Service.getHostedZoneByName(domainName);
		return hostedZone;
	}
	
	@RequestMapping(value="resourceRecordSets/{hostedZoneId}", method=RequestMethod.GET)
	public List<ResourceRecordSet> getResourceRecordSet(@PathVariable String hostedZoneId) {
		List<ResourceRecordSet> resourceRecordSets = route53Service.getResourceRecordSet(hostedZoneId);		
		return resourceRecordSets;
	}
	
	@RequestMapping(value="hostedZones", method=RequestMethod.POST)
	public HostedZone createHostedZone(@RequestBody Map<String, String> requestBody) {
		HostedZone hostedZone = route53Service.createHostedZone(requestBody.get("domainName"));
		return hostedZone;
	}
	
	@RequestMapping(value="hostedZones/{hostedZoneID}/recordSet", method=RequestMethod.POST)
	public void createRecordSet(@PathVariable String hostedZoneID, @RequestBody RecordSetForm recordSetForm) {
		route53Service.createRecordSet(hostedZoneID, recordSetForm.getDomainName(), recordSetForm.getType(), recordSetForm.getValue());
	}
	
	@RequestMapping(value="hostedZones/{hostedZoneID}", method=RequestMethod.DELETE)
	public void deleteHostedZone(@PathVariable String hostedZoneID) {
		route53Service.deleteHostedZone(hostedZoneID);
	}

}
