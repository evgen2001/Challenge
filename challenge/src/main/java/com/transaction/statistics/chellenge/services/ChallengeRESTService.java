package com.transaction.statistics.chellenge.services;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.transaction.statistics.chellenge.model.Statistics;
import com.transaction.statistics.chellenge.model.Transaction;


@RestController
public class ChallengeRESTService {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Map<Long, Double> statMap = Collections.synchronizedMap(new TreeMap(Collections.reverseOrder()));
	private static long SIXTY_SECONDS = 60000;
	
	@Autowired
	Statistics statistics;

	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/transaction", method=RequestMethod.POST)
	public ResponseEntity receiveIncome(@RequestBody Transaction receivedTransaction) 
	{ 
		ResponseEntity response = new ResponseEntity(HttpStatus.CREATED);
		if(isTransactionOlderThanMinute(receivedTransaction.getTimestamp()))
		{
			response = new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		statMap.put(receivedTransaction.getTimestamp(), receivedTransaction.getAmount());
		
		return response;
	}


	private boolean isTransactionOlderThanMinute(long transactionTimeStamp) {
		
		return System.currentTimeMillis() - transactionTimeStamp > SIXTY_SECONDS;
	}


	@RequestMapping(value = "/statistics", method = RequestMethod.GET)
	public Statistics getStatistics() {

		return statistics;


	}

	@Scheduled(fixedRate = 1000)
	private Statistics retrieveStatisticsForTheLastMinute() {
		TreeMap<Long, Double> newStatMap =  new TreeMap<Long, Double>(Collections.reverseOrder());
		double min = 0;
		double max = 0;
		double avg = 0;
		double sum = 0;
		long count = 0;

		for(Map.Entry<Long,Double> entry : statMap.entrySet())
		{
			Long transactionTimeStamp = entry.getKey();
			Double transactionAmmount = entry.getValue();
			if(!isTransactionOlderThanMinute(transactionTimeStamp))
			{
				min = transactionAmmount;
				sum = sum + transactionAmmount;
				count ++;
				avg = sum / count;
				if(transactionAmmount < min)
				{
					min = transactionAmmount;
				}
				if(transactionAmmount > max)
				{
					max = transactionAmmount;
				}
				newStatMap.put(transactionTimeStamp, transactionAmmount);

			}

			statistics.setAvg(avg);
			statistics.setCount(count);
			statistics.setMax(max);
			statistics.setMin(min);
			statistics.setSum(sum);
		}
		statMap.clear();
		statMap.putAll(newStatMap);

		return statistics;


	}



}