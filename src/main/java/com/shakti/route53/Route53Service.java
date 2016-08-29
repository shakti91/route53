package com.shakti.route53;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.route53.AmazonRoute53Client;
import com.amazonaws.services.route53.model.Change;
import com.amazonaws.services.route53.model.ChangeAction;
import com.amazonaws.services.route53.model.ChangeBatch;
import com.amazonaws.services.route53.model.ChangeResourceRecordSetsRequest;
import com.amazonaws.services.route53.model.CreateHostedZoneRequest;
import com.amazonaws.services.route53.model.CreateHostedZoneResult;
import com.amazonaws.services.route53.model.DeleteHostedZoneRequest;
import com.amazonaws.services.route53.model.GetHostedZoneRequest;
import com.amazonaws.services.route53.model.GetHostedZoneResult;
import com.amazonaws.services.route53.model.HostedZone;
import com.amazonaws.services.route53.model.ListHostedZonesByNameRequest;
import com.amazonaws.services.route53.model.ListHostedZonesByNameResult;
import com.amazonaws.services.route53.model.ListHostedZonesRequest;
import com.amazonaws.services.route53.model.ListHostedZonesResult;
import com.amazonaws.services.route53.model.ListResourceRecordSetsRequest;
import com.amazonaws.services.route53.model.ListResourceRecordSetsResult;
import com.amazonaws.services.route53.model.RRType;
import com.amazonaws.services.route53.model.ResourceRecord;
import com.amazonaws.services.route53.model.ResourceRecordSet;

@Service
public class Route53Service {
	
	private final String AWS_ACCESS_KEY = "yourAwsAccessKey";
	private final String AWS_SECRET_KEY  = "yourAwsSecretKey";
	
	public List<HostedZone> listHostedZones() {
		AmazonRoute53Client route53Client = new AmazonRoute53Client(new BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY));
		ListHostedZonesRequest request = new ListHostedZonesRequest();
		ListHostedZonesResult result = route53Client.listHostedZones(request);
		List<HostedZone> hostedZones = result.getHostedZones();
		return hostedZones;
	}
	
	public List<HostedZone> listAllHostedZones() {
		AmazonRoute53Client route53Client = new AmazonRoute53Client(new BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY));
		List<HostedZone> hostedZones = new ArrayList<>();
		String marker = null;
		do {
			ListHostedZonesRequest request = new ListHostedZonesRequest();
			request.setMarker(marker);
			ListHostedZonesResult result = route53Client.listHostedZones(request);
			hostedZones.addAll(result.getHostedZones());
			marker = result.getNextMarker();
		}
		while(marker != null);
		
		return hostedZones;
	}
	
	public List<HostedZone> listHostedZonesByName() {
		AmazonRoute53Client route53Client = new AmazonRoute53Client(new BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY));
		ListHostedZonesByNameRequest request = new ListHostedZonesByNameRequest();
		ListHostedZonesByNameResult result = route53Client.listHostedZonesByName(request);
		List<HostedZone> hostedZones = result.getHostedZones();
		return hostedZones;
	}
	
	public HostedZone getHostedZoneById(String id) {
		AmazonRoute53Client route53Client = new AmazonRoute53Client(new BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY));
		GetHostedZoneRequest request = new GetHostedZoneRequest();
		request.setId(id);
		GetHostedZoneResult result = route53Client.getHostedZone(request);
		HostedZone hostedZone = result.getHostedZone();
		return hostedZone;
	}
	
	/**
	 * @param domainName : This must be a fully-specified domain, for example, www.example.com. with trailing dot
	 * @return HostedZone
	 */
	public HostedZone getHostedZoneByName(String domainName) {
		AmazonRoute53Client route53Client = new AmazonRoute53Client(new BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY));
		ListHostedZonesByNameRequest request = new ListHostedZonesByNameRequest();
		request.setMaxItems("1");
		request.setDNSName(domainName);
		ListHostedZonesByNameResult result = route53Client.listHostedZonesByName(request);
		HostedZone hostedZone = null;
		for (HostedZone zone : result.getHostedZones()) {
			hostedZone = zone.getName().equals(domainName) ? zone : null;
		}
		return hostedZone;
	}
	
	public List<ResourceRecordSet> getResourceRecordSet(String hostedZoneId) {
		AmazonRoute53Client route53Client = new AmazonRoute53Client(new BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY));
		ListResourceRecordSetsRequest request = new ListResourceRecordSetsRequest();
		request.setHostedZoneId(hostedZoneId);
		ListResourceRecordSetsResult result = route53Client.listResourceRecordSets(request);
		return result.getResourceRecordSets();
	}
	
	public HostedZone createHostedZone(String domainName) {
		AmazonRoute53Client route53Client = new AmazonRoute53Client(new BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY));
		CreateHostedZoneRequest request = new CreateHostedZoneRequest();
		request.setName(domainName);
		// A unique string that identifies the request and that allows failed requests to be retried
		request.setCallerReference(new Date().toString());
		CreateHostedZoneResult result = route53Client.createHostedZone(request);
		return result.getHostedZone();
	}
	
	public void createRecordSet(String hostedZoneID, String domainName, RRType type, String value) {
		AmazonRoute53Client route53Client = new AmazonRoute53Client(new BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY));
		ResourceRecord resourceRecord = new ResourceRecord();
		resourceRecord.setValue(value);
		
		List<ResourceRecord> resourceRecords = new ArrayList<ResourceRecord>();
		resourceRecords.add(resourceRecord);

		ResourceRecordSet recordSet = new ResourceRecordSet();
		recordSet.setName(domainName);
		recordSet.setType(type);
		recordSet.setTTL(300L);
		recordSet.setResourceRecords(resourceRecords);

		Change change = new Change(ChangeAction.CREATE, recordSet);

		List<Change> changes = new ArrayList<Change>();
		changes.add(change);
		ChangeBatch changeBatch = new ChangeBatch(changes);

		ChangeResourceRecordSetsRequest request = new ChangeResourceRecordSetsRequest();
		request.setHostedZoneId(hostedZoneID);
		request.setChangeBatch(changeBatch);

		route53Client.changeResourceRecordSets(request);
	}
	
	public void deleteHostedZone(String hostedZoneID) {
		AmazonRoute53Client route53Client = new AmazonRoute53Client(new BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY));
		
		// fetch resource record sets
		ListResourceRecordSetsRequest recordSetsRequest = new ListResourceRecordSetsRequest();
		recordSetsRequest.setHostedZoneId(hostedZoneID);
		ListResourceRecordSetsResult recordSetsResult = route53Client.listResourceRecordSets(recordSetsRequest);
		List<ResourceRecordSet> resourceRecordSets = recordSetsResult.getResourceRecordSets();
		
		List<Change> changes = new ArrayList<Change>();
		for (ResourceRecordSet recordSet : resourceRecordSets) {
			// ignore SOA and NS record sets since they cannot be deleted
			if (!recordSet.getType().equals(RRType.NS.toString()) && !recordSet.getType().equals(RRType.SOA.toString())) {
				Change change = new Change(ChangeAction.DELETE, recordSet);
				changes.add(change);
			}
		}
		
		// delete resource record sets
		if (changes.size() > 0) {
			ChangeBatch changeBatch = new ChangeBatch(changes);
			ChangeResourceRecordSetsRequest recordSetRequest = new ChangeResourceRecordSetsRequest();
			recordSetRequest.setHostedZoneId(hostedZoneID);
			recordSetRequest.setChangeBatch(changeBatch);

			route53Client.changeResourceRecordSets(recordSetRequest);
		}
		
		// delete hosted zone
		DeleteHostedZoneRequest request = new DeleteHostedZoneRequest();
		request.setId(hostedZoneID);
		route53Client.deleteHostedZone(request);
	}
}
