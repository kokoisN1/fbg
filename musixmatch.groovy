//@Grapes([
//  @Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.6' ),
//  @Grab(group='org.apache.commons', module='commons-csv', version='1.9.0'),
//  @Grab('org.mongodb:mongodb-driver:3.7.0')
//])

import groovy.json.JsonSlurper
import org.apache.commons.csv.CSVPrinter
/*
import com.mongodb.MongoClient
import com.mongodb.DBCollection
import com.mongodb.DB
import com.mongodb.BasicDBObject
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.Document
import org.bson.types.ObjectId
*/
//Use musixmatch’s API to write a script that receives an argument and queries
//the api with the argument as a query string.
//Create a list of songs with the following attributes:
//- Song lyrics contains the argument supplied
//- Song language is English
//- Album release date is prior to 01-01-2010
//If no argument is passed, the default argument is “car”
//Save the results into CSV file in the following format: song name, performer name,
//album name, song share URL

class Interactwithmusixmatch {
        private static String _musixmatchAPIURL = "http://api.musixmatch.com/ws/1.1/track.search"
        private static String _musixmatchAPIURLAlbum = "http://api.musixmatch.com/ws/1.1/album.get"
        private static String _lyricsToFilter = "love"
        private static String _APIKey = "57aef64c1f17a2ab6d3dc47e2b700bcc"
        private static String _dateMinFilter = "20100101"
        private static String _fileName = 'c:\\temp\\musixmatch.csv'
        private static String _FILE_HEADER = ['song name','performer name','album name','song share URL']
//        private static MongoClient _mongoClient
        private static String _databaseName = 'musixmatch'


        def static String getResultsPage(String baseURL, String lyricsFilter, String minDate , String APIKey)
        {
                // q_lyrics - search for lyrics in the track_list
                // f_track_release_group_first_release_date_min - limit the search to tracks newer then the paarmater
                // apikey - authntication key
                // f_has_lyrics - might be redundent, but may speedup the search

                String requestURLF = "$baseURL?q=$lyricsFilter&f_track_release_group_first_release_date_max=$minDate&apikey=$APIKey"
                println "requestURLF" + requestURLF
                //requestURLF =  "http://api.musixmatch.com/ws/1.1/album.get?album.get?album_id=1425041"
                //println "requestURLF" + requestURLF
                try {
					return new URL(requestURLF).getText()
                } catch (Exception e) {
                    println "Exception, message is " + e.message
                }
        }// getResultsPage()

        def static String getTrackLyrics(Integer TrackID , String APIKey)
        {
                String requestURL = "http://api.musixmatch.com/ws/1.1/http://api.musixmatch.com/ws/1.1/album.get?track_id$TrackID=&apikey=$APIKey"
                println "requestURL" + requestURL
                try {
		def res = new URL(requestURL).getText()
            	def parser = new JsonSlurper()
		def json = parser.parseText(res)
                    println "json = " + json
                    return "koko"
                } catch (Exception e) {
                    println "Exception, message is " + e.message
                }
        }// getTrackLyrics()


		static  main(args)
        {
			println "Calling musixmatch..."
			def res = getResultsPage(_musixmatchAPIURL,_lyricsToFilter,_dateMinFilter,_APIKey)
			def parser = new JsonSlurper()
			def json = parser.parseText(res)
			println "json = " + json

			new File(_fileName).withWriter { fileWriter ->
			    def csvFilePrinter = new CSVPrinter(fileWriter, org.apache.commons.csv.CSVFormat.DEFAULT)
            		    csvFilePrinter.printRecord(_FILE_HEADER)
   		    	json.message.body.track_list.each{t ->
			    csvFilePrinter.printRecord([t.track.track_name, t.track.artist_name, t.track.album_name, t.track.track_share_url])
			    def TLyrics = getTrackLyrics(t.track.track_id,_APIKey)
			    println "Track found, Name: " + t.track.track_name +" Artist : "+ t.track.artist_name+ " Album : "+  t.track.album_name+"URL :"+ t.track.track_share_url
					
			}

	                    }
			println "Found " + json.message.body.track_list.size() + " Tracks"
			println "end of run"
        } // main()
} // class Interactwithmusixmatch {}
