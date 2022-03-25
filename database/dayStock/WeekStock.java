import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class WeekStock {

	public static void main(String[] args) throws IOException, ParseException {
//		"code_number","Name","Market","Dept","Close",	// 0~4
//		"ChangeCode","Changes","ChagesRatio","Open","High","Low",	// 5~10
//		"Volume","Amount","Marcap","Stocks","MarketId","Rank","Date"	// 11~17
		StringBuilder sb=new StringBuilder();
		BufferedReader br;
		String[] kospi_code={"373220", "404990", "396690", "402340", "400760", "377300", "381970", "329180", "395400", "271940", "377190", "139990", "089860", "372910", "259960", "178920", "323410", "137310", "097520", "383800", "383220", "380440", "361610", "302440", "378850", "950210", "248070", "375500", "365550", "009900", "244920", "339770", "352820", "363280", "357120", "348950", "357250", "350520", "334890", "326030", "353200", "344820", "338100", "308170", "322000", "272210", "317400", "330590", "013890", "336260", "336370", "036420", "003670", "307950", "192650", "192080", "316140", "298690", "267850", "306200", "293480", "010400", "293940", "300720", "091810", "286940", "298040", "298050", "298020", "298000", "088260", "294870", "018250", "068270", "284740", "285130", "282330", "092780", "272450", "281820", "282690", "272550", "055490", "280360", "271980", "035720", "271560", "268280", "267290", "251270", "267270", "267260", "267250", "264900", "145720", "111110", "143210", "241560", "207940", "234080", "241590", "229640", "204210", "249420", "248170", "016740", "026960", "123890", "034830", "195870", "070960", "101530", "004440", "194370", "226320", "075580", "214330", "089590", "227840", "079550", "039570", "002690", "095570", "214320", "214420", "085620", "214390", "210980", "138250", "145210", "213500", "210540", "028260", "112610", "018260", "204320", "200880", "192400", "133820", "027410", "192820", "185750", "006880", "183190", "064350", "180640", "181710", "175330", "155660", "168490", "170900", "013870", "037560", "163560", "161890", "161390", "161000", "068400", "155900", "014710", "153360", "152550", "079980", "145270", "007070", "129260", "145990", "039130", "078520", "023000", "031430", "140910", "047810", "071840", "069640", "138490", "139480", "139130", "053210", "019440", "138040", "023350", "138930", "020150", "011210", "136490", "134380", "134790", "082740", "117580", "126560", "058860", "130660", "900140", "030790", "081660", "058850", "057050", "002150", "128820", "122900", "128940", "105840", "033920", "102460", "123690", "123700", "060980", "032830", "031440", "088350", "120030", "120110", "119650", "071320", "115390", "012160", "118000", "052690", "114090", "034730", "032560", "000080", "082640", "047400", "093240", "039490", "111770", "053690", "028100", "071970", "108670", "105630", "109070", "107590", "035420", "026940", "105560", "104700", "103590", "103140", "011070", "017180", "100220", "102260", "102280", "032640", "101140", "020560", "100840", "100250", "085310", "051600", "093230", "092440", "044450", "094280", "033270", "092200", "097950", "028670", "097230", "096760", "096770", "029780", "096300", "001780", "095720", "094800", "090370", "093370", "093050", "044380", "044820", "034590", "009770", "092220", "092230", "089470", "091090", "090350", "090430", "032350", "090080", "049800", "035510", "088790", "088980", "023530", "086280", "084870", "086790", "001560", "079430", "084010", "075180", "083420", "084690", "071950", "084680", "084670", "042700", "004890", "010960", "073240", "079160", "081000", "014830", "041650", "023810", "078930", "077500", "058730", "034220", "078000", "077970", "034310", "019680", "024110", "074610", "025750", "007660", "035250", "037270", "072710", "071050", "072130", "034120", "036570", "071090", "069730", "006890", "011500", "069960", "069460", "069620", "027740", "036530", "069260", "033530", "058430", "068290", "067830", "030610", "009240", "034300", "017370", "066570", "024720", "064960", "037710", "020760", "033180", "063160", "055550", "021240", "058650", "051900", "051910", "047040", "047050", "042660", "042670", "051630", "049770", "034020", "010040", "016450", "016710", "003220", "036460", "007120", "000850", "036580", "033780", "009540", "024090", "006840", "006650", "035150", "035000", "033250", "033240", "030200", "019490", "030000", "026890", "029460", "024890", "017940", "018670", "023590", "014440", "011790", "010600", "025890", "024900", "023960", "023800", "012320", "025530", "031820", "000660", "023150", "005320", "023450", "028050", "029530", "015360", "016800", "024070", "030720", "025560", "030210", "012630", "017900", "025540", "003160", "011930", "019180", "018880", "021820", "027970", "006740", "000910", "020120", "020000", "017800", "016580", "013580", "021050", "012750", "015890", "016590", "011690", "025860", "018500", "011300", "011200", "012610", "017810", "017390", "025820", "011090", "007460", "025620", "017960", "025000", "010580", "007980", "009440", "008420", "016880", "007310", "008500", "014790", "010120", "009190", "000760", "014820", "010140", "009680", "010690", "000390", "009450", "012030", "005090", "005750", "005880", "008560", "005870", "017550", "011170", "001430", "008770", "014580", "013520", "004080", "006060", "014530", "002140", "016090", "001620", "017040", "011390", "011810", "010130", "012600", "004720", "009200", "001500", "008870", "008110", "005820", "008490", "014910", "003580", "015860", "014160", "012280", "019170", "002210", "012170", "009420", "011330", "012800", "001140", "013360", "013700", "002760", "007610", "004920", "009290", "011420", "004270", "001940", "009410", "007280", "009180", "002170", "017670", "010640", "003650", "004380", "002840", "007860", "013000", "012330", "015540", "001390", "009320", "001450", "005440", "015760", "007690", "006280", "009160", "008600", "015230", "007160", "014130", "008040", "009140", "009580", "014990", "010420", "013570", "003610", "015590", "014680", "010820", "006040", "008060", "003680", "015020", "011280", "002310", "010660", "011150", "001770", "018470", "005850", "009970", "012510", "003850", "008250", "012690", "006370", "006980", "002920", "002220", "002450", "002460", "007340", "000890", "001290", "003010", "014280", "009810", "008730", "009270", "004140", "002960", "003000", "005810", "001270", "012200", "004710", "016610", "008930", "005500", "005490", "002410", "005690", "001230", "006340", "005950", "016360", "002810", "001750", "011780", "003470", "002620", "015260", "009460", "005720", "006660", "004490", "011000", "009310", "010100", "005070", "004830", "001720", "001200", "003460", "011700", "005420", "000500", "010950", "012450", "011230", "004020", "004770", "004560", "006090", "003530", "009470", "001510", "010770", "007110", "016380", "010780", "002360", "003830", "007810", "004170", "010060", "000520", "006490", "003120", "000720", "002710", "003060", "007700", "008700", "010620", "006360", "006110", "006200", "006400", "009150", "005960", "009070", "003070", "005180", "005250", "000480", "004310", "003920", "008350", "003350", "001880", "011760", "006260", "001740", "002100", "004870", "007590", "004430", "002780", "004090", "000650", "000430", "001080", "004700", "004360", "008970", "002880", "001260", "005680", "005800", "002630", "001550", "005430", "004910", "004690", "003080", "002600", "001570", "003960", "002070", "003570", "004960", "004370", "003720", "004980", "004970", "002870", "001630", "005740", "002390", "001380", "006120", "007540", "003280", "001250", "002990", "001470", "001820", "001020", "001520", "000880", "001060", "000670", "001340", "000180", "005110", "004450", "000040", "002240", "004100", "002820", "004840", "002350", "004060", "004000", "000020", "000210", "001120", "002720", "003240", "005030", "006390", "000590", "005390", "000990", "006570", "000220", "000860", "003540", "005940", "006800", "002700", "001360", "000810", "003230", "000370", "008260", "000230", "000490", "001800", "004540", "001420", "002020", "005930", "000300", "001460", "003620", "003410", "005610", "000540", "010050", "007570", "002320", "002200", "005380", "005360", "009830", "002030", "006380", "004990", "001070", "004410", "003560", "000680", "001210", "000140", "000320", "000270", "004800", "001040", "000150", "005010", "005830", "002900", "003090", "003780", "003520", "002380", "005300", "003200", "002270", "002790", "006220", "004150", "007210", "000400", "001130", "001680", "003550", "000640", "003690", "000970", "003300", "004250", "003030", "001440", "001790", "000070", "002420", "000240", "000950", "003490", "001530", "000100", "000120", "000060", "000050", "000700", "003480"};
		String[] kosdaq_code={"408920", "376930", "146320", "371950", "330730", "406760", "365900", "370090", "251120", "409570", "405350", "211050", "137080", "136410", "276040", "377330", "179530", "102370", "404950", "353590", "200350", "400560", "396770", "397880", "400840", "298870", "199800", "397500", "377480", "361570", "222160", "290090", "372800", "311320", "376300", "357880", "389030", "376180", "348370", "377450", "382800", "114840", "261780", "382480", "391710", "376290", "159010", "382840", "388220", "391060", "257720", "260970", "377220", "099430", "393360", "387310", "388800", "273640", "203400", "388790", "308080", "099390", "315640", "058970", "367000", "376980", "195940", "386580", "270660", "377030", "365270", "352910", "357580", "303530", "232680", "380320", "376190", "187660", "383310", "276730", "363250", "377630", "361670", "252990", "352480", "357230", "377400", "294570", "351330", "059270", "373340", "367480", "333620", "289220", "361390", "347700", "314930", "367360", "950220", "356890", "334970", "247660", "372290", "338220", "373200", "340930", "189330", "322310", "239890", "363260", "262840", "366330", "277810", "321820", "163730", "348030", "352700", "086710", "236810", "368770", "367460", "369370", "357550", "314130", "335810", "347860", "365590", "064850", "353060", "356860", "352940", "354200", "367340", "317690", "352770", "318020", "129890", "265740", "199820", "299030", "340570", "348150", "330860", "290690", "348350", "347000", "301300", "214610", "304840", "348210", "053080", "126340", "359090", "323990", "318410", "355150", "347770", "291650", "294090", "293490", "347740", "353490", "332370", "331920", "950190", "337930", "060850", "032300", "357780", "164060", "332570", "101360", "353070", "347890", "298540", "225220", "304100", "343510", "277880", "262260", "351340", "950200", "290520", "330350", "169330", "229000", "297890", "351320", "298060", "353190", "353810", "223250", "349720", "347140", "198080", "237820", "340350", "092190", "204270", "274090", "294140", "294630", "344050", "065370", "342550", "341160", "340440", "311690", "062970", "327260", "339950", "340120", "288330", "340360", "336570", "235980", "226330", "322510", "124560", "335870", "302550", "337450", "335890", "290510", "336060", "278650", "321550", "297090", "103840", "190650", "306040", "216080", "214260", "084850", "333430", "279600", "332710", "333050", "322180", "331520", "317530", "272110", "317870", "318010", "300120", "158430", "234690", "332290", "331380", "330990", "244460", "317120", "320000", "256150", "329560", "311390", "195500", "328380", "286750", "317830", "228670", "186230", "282880", "317330", "317770", "318000", "148150", "313760", "317850", "241840", "300080", "234340", "289010", "251970", "312610", "323280", "323230", "293780", "308100", "305090", "322780", "321260", "253840", "307930", "319660", "319400", "950180", "317240", "125210", "228760", "099750", "100790", "247540", "299660", "278280", "246960", "104620", "053580", "238200", "263050", "290550", "307180", "310200", "310870", "309930", "299900", "307750", "298380", "100590", "110020", "270870", "307870", "302430", "290120", "128540", "307280", "299910", "900340", "111710", "179290", "117730", "290660", "246710", "263690", "217330", "027360", "208340", "263020", "227100", "194700", "268600", "290670", "285490", "290650", "306620", "108490", "153710", "288620", "293580", "290720", "110790", "299170", "219750", "257370", "291230", "197140", "303030", "290740", "173130", "089970", "086820", "290380", "110990", "275630", "290270", "080720", "204020", "297570", "226950", "175250", "289080", "245620", "284620", "037030", "016790", "258830", "122310", "287410", "950170", "253590", "263700", "064510", "277070", "226400", "183490", "006620", "154030", "260660", "042000", "219420", "267790", "255220", "264660", "269620", "187870", "281740", "260930", "241770", "234300", "066360", "007680", "138580", "253450", "148140", "950160", "265560", "234100", "263810", "255440", "277410", "263920", "243840", "179900", "171090", "259630", "174900", "263540", "263750", "256940", "263600", "258610", "270520", "140670", "900310", "273060", "118990", "263800", "263860", "263720", "091990", "181340", "238490", "251630", "227610", "272290", "003380", "261200", "267320", "250000", "267980", "225190", "264850", "161580", "256630", "166090", "258790", "251370", "265520", "263770", "063760", "264450", "178320", "183300", "246720", "217480", "002800", "083500", "087260", "241820", "140070", "206650", "256840", "241520", "246690", "147760", "196300", "203450", "215600", "254120", "241790", "220100", "144960", "156100", "176440", "220180", "237880", "900300", "238120", "252500", "216050", "239610", "241710", "900290", "900280", "189300", "072990", "950140", "238090", "050960", "201490", "250930", "241690", "250060", "242040", "234920", "900270", "230360", "208860", "073560", "123010", "174880", "071460", "142760", "237750", "900260", "237690", "144510", "148250", "239340", "243070", "240810", "228850", "225330", "228340", "222110", "211270", "230980", "115180", "065660", "236200", "142210", "900250", "109610", "224060", "232140", "191410", "223310", "145020", "222980", "206560", "013310", "047920", "217730", "133750", "225530", "140860", "230240", "122640", "222040", "058110", "217820", "221840", "180400", "127160", "221980", "212560", "227950", "214370", "185490", "190510", "056090", "226340", "115960", "182400", "226360", "092870", "214870", "225430", "226440", "225590", "225570", "217190", "219130", "175140", "222080", "220630", "224110", "196700", "131760", "222810", "222800", "189980", "222420", "094360", "127710", "214430", "067730", "220260", "214450", "219550", "087010", "218410", "094170", "214180", "178780", "160600", "217600", "166480", "214310", "177350", "217270", "218150", "160980", "217500", "087600", "217620", "187420", "215380", "215480", "215790", "215360", "215200", "215090", "215100", "214680", "195990", "215000", "214150", "214270", "189690", "213420", "060480", "206640", "204630", "208640", "193250", "200470", "200670", "208710", "189860", "067390", "080580", "208350", "160550", "124500", "142280", "187220", "208370", "208140", "149980", "084650", "200710", "200780", "207760", "196170", "206400", "140520", "173940", "205470", "200230", "204840", "204620", "196490", "205500", "205100", "191420", "192440", "182690", "143540", "203650", "041920", "194480", "203690", "192410", "198440", "187270", "177830", "090410", "004650", "192390", "059120", "200130", "105550", "196450", "071850", "187790", "192250", "138080", "053300", "154040", "090850", "184230", "049080", "150840", "085810", "170030", "067570", "134580", "182360", "171120", "171010", "161570", "138360", "076610", "168330", "131970", "150900", "170920", "119850", "092040", "130500", "170790", "151860", "089600", "950130", "141080", "158310", "097800", "104540", "159580", "114810", "153490", "099190", "141020", "159910", "950110", "046970", "113810", "155650", "149950", "121850", "151910", "153460", "106520", "137400", "141000", "072950", "143240", "126870", "147830", "091590", "140410", "104830", "127120", "130580", "131090", "100660", "115480", "139670", "121800", "138070", "123570", "122870", "136510", "089530", "089030", "101240", "007820", "115530", "090360", "131220", "131390", "143160", "137950", "040910", "123330", "139050", "089980", "138610", "109080", "108380", "019770", "137940", "131100", "138690", "122450", "121440", "136540", "136480", "134060", "104480", "008470", "121600", "093320", "130740", "131180", "061970", "126880", "033560", "048530", "119860", "131370", "064290", "131290", "131400", "120240", "128660", "131030", "096690", "033830", "033170", "041460", "105740", "126600", "123420", "111870", "123860", "058400", "069140", "119830", "101930", "078650", "068940", "123840", "117670", "119500", "068240", "096530", "122990", "126700", "126640", "123410", "123040", "067920", "119610", "106190", "114120", "121890", "123750", "089850", "046120", "070300", "108320", "079970", "071200", "100030", "122690", "900110", "900100", "115500", "900120", "115450", "122350", "115610", "115310", "106080", "101330", "115570", "115440", "050860", "114630", "080530", "900070", "099520", "114450", "104460", "096640", "112040", "108860", "088290", "114190", "115160", "109960", "099410", "101000", "109820", "082920", "042520", "104200", "052860", "105330", "109740", "095700", "108230", "100130", "103230", "063080", "109860", "102120", "101730", "102710", "099440", "099220", "101680", "010240", "101170", "081150", "098660", "071670", "101490", "100120", "102940", "101400", "106240", "086890", "104040", "078070", "086900", "058630", "059100", "067630", "092130", "100700", "098120", "083470", "100090", "101390", "076080", "101670", "094840", "063170", "047560", "096240", "101160", "099320", "064480", "098460", "067000", "018620", "095610", "053280", "096870", "059210", "097870", "091440", "096040", "097780", "096630", "068330", "093920", "067010", "096350", "069920", "096610", "092070", "094820", "086670", "095190", "092300", "093640", "095910", "081580", "073110", "094970", "080520", "095340", "085910", "095500", "027580", "086040", "090740", "057540", "094940", "095270", "093190", "091340", "072770", "094480", "086520", "065150", "078340", "095660", "092600", "069410", "093380", "086450", "064820", "091580", "092460", "073540", "091120", "094850", "094860", "078020", "093520", "048260", "091970", "092730", "090460", "039200", "089010", "090470", "088130", "084110", "089150", "091700", "086390", "090710", "060540", "066310", "067280", "089890", "080470", "089140", "089790", "090150", "088390", "089230", "043150", "086960", "038060", "083660", "062860", "063570", "086980", "068050", "085370", "039290", "078860", "088910", "054950", "084730", "086060", "022220", "088800", "068760", "083450", "019990", "079370", "046110", "085670", "084370", "083790", "064550", "084990", "083640", "085660", "078140", "067900", "083550", "084180", "050540", "080420", "083650", "079190", "082800", "043910", "079000", "041020", "078590", "038070", "079940", "077360", "083930", "067310", "083310", "082660", "080010", "058220", "078160", "082850", "080160", "078890", "050890", "082210", "082270", "075130", "073490", "079950", "079960", "080220", "079170", "079810", "080000", "052220", "079650", "052900", "066910", "049950", "052460", "072870", "073010", "046440", "078600", "054450", "078150", "067990", "051360", "072520", "078130", "068790", "078940", "048870", "065350", "039340", "064260", "011080", "050090", "029960", "057030", "065770", "074430", "066700", "066900", "078350", "075970", "069540", "073190", "054090", "041910", "039670", "067770", "070590", "073570", "073640", "065680", "057880", "060570", "066410", "067160", "074600", "071280", "069510", "067170", "054780", "041440", "072020", "065510", "072470", "068930", "069110", "069330", "064760", "052710", "065950", "047310", "056000", "041520", "065560", "064240", "066790", "069080", "067080", "066590", "067290", "066130", "046140", "065450", "065130", "065440", "049630", "021650", "051370", "065650", "065570", "066980", "016100", "042040", "066970", "066670", "066430", "047820", "064090", "049960", "053290", "064520", "035600", "065170", "034230", "041590", "042600", "065500", "060900", "066620", "052770", "065420", "064800", "039980", "048410", "058610", "045340", "065710", "065690", "066110", "043710", "060560", "036010", "060280", "060230", "046940", "057680", "060150", "065530", "063440", "051160", "065060", "049180", "060720", "061040", "050760", "054210", "050120", "054410", "060310", "061250", "053980", "054050", "039420", "039440", "049550", "054670", "060590", "049070", "060370", "051380", "053660", "053270", "034950", "060260", "060380", "046310", "058530", "049720", "052670", "046890", "013990", "058450", "059090", "050320", "054930", "035200", "054040", "048470", "060300", "042500", "058420", "040350", "054300", "047080", "060250", "056730", "053260", "060240", "041930", "054220", "014940", "043590", "058470", "045300", "056190", "056700", "054940", "043370", "054620", "056360", "043260", "038340", "051390", "054540", "053350", "054630", "056080", "044490", "053950", "053700", "054920", "041960", "053160", "052400", "054180", "049470", "053050", "043090", "053110", "054800", "047770", "053800", "040300", "058820", "035900", "053590", "021320", "053060", "053610", "052790", "053450", "053030", "052420", "053620", "049520", "052300", "048910", "051980", "051500", "049120", "043360", "052330", "052600", "052020", "043220", "033790", "051780", "052190", "051490", "038870", "045660", "045390", "050110", "052260", "036540", "038500", "048550", "049830", "044060", "024850", "048770", "049430", "044480", "046390", "014620", "048430", "017480", "049480", "036670", "042510", "039830", "036190", "046070", "041830", "014570", "045520", "022100", "044180", "044960", "032080", "010470", "044340", "042940", "048830", "000250", "044780", "045510", "045100", "040160", "043340", "043650", "037950", "039030", "038540", "018680", "039860", "046210", "045060", "043100", "042370", "045970", "040420", "007370", "041140", "043610", "036890", "039010", "009300", "043200", "042110", "039740", "039840", "042420", "039560", "038460", "032820", "001540", "038530", "039310", "041190", "036640", "031330", "030190", "038620", "039020", "038950", "036800", "040610", "038880", "038680", "041510", "010280", "037760", "036480", "038110", "032500", "039610", "039240", "038290", "037440", "038390", "036830", "036810", "036690", "037370", "036620", "037350", "038010", "001810", "036630", "036710", "037070", "016250", "036560", "035890", "036930", "036030", "032190", "005290", "003100", "026040", "036180", "036170", "037230", "037460", "037400", "033640", "037330", "036090", "036200", "024800", "027050", "036120", "035290", "027040", "027830", "036000", "035760", "035810", "035460", "035620", "035610", "020710", "012790", "034940", "033230", "035080", "005160", "005990", "034810", "025770", "033540", "033500", "030350", "033160", "033310", "033320", "009520", "013810", "033340", "033200", "033290", "015710", "033100", "033050", "033130", "032980", "019210", "026150", "031310", "032960", "032680", "032940", "008290", "032750", "032850", "032860", "032790", "032800", "032620", "032580", "032540", "028080", "032280", "012340", "031510", "031860", "031980", "011560", "024740", "030960", "030530", "030520", "021880", "029480", "021040", "028300", "027710", "023600", "018310", "003800", "014190", "012700", "018000", "025980", "026910", "017250", "015750", "020180", "006730", "025950", "025900", "025870", "025880", "002680", "025550", "025440", "025320", "024940", "024950", "014100", "024840", "024910", "004780", "013120", "024830", "024810", "024880", "024120", "017000", "024060", "023910", "001840", "023900", "017650", "010170", "023790", "023770", "023760", "005860", "012860", "023460", "006050", "023410", "023440", "006910", "018290", "023160", "007530", "001000", "012620", "011370", "018700", "014470", "007720", "013720", "006580", "011320", "018120", "009620", "005670", "007330", "006140", "019010", "009780", "020400", "014200", "005710", "016600", "004590", "016670", "000440", "014970", "011040", "008370", "007390", "002290", "019540", "007770", "017890", "003310", "019660", "017510", "021080", "008830", "002230", "009730", "016920", "013030", "019550", "019570", "019590", "006920"};
		String[] konex_code={"169670", "403360", "254160", "402420", "248020", "393210", "318660", "379390", "257990", "322190", "253610", "286000", "122830", "348840", "266470", "322970", "329020", "344860", "343090", "341310", "238170", "317860", "336040", "250030", "337840", "331660", "093510", "149300", "327970", "327610", "309900", "323350", "279060", "311060", "308700", "303360", "207490", "288490", "140610", "302920", "258050", "276240", "299480", "299670", "163430", "217910", "285770", "281310", "236030", "278990", "180060", "270020", "260870", "267060", "162120", "233250", "271850", "270210", "266170", "267810", "225860", "266870", "227420", "242350", "258250", "199290", "258540", "232830", "148780", "243870", "251280", "191600", "212310", "224020", "066830", "246250", "245450", "208850", "244880", "112190", "240340", "238500", "236340", "067370", "206950", "232530", "233990", "135160", "215570", "234070", "044990", "225850", "217950", "092590", "229500", "224810", "200580", "224760", "222670", "086220", "223220", "221800", "216400", "103660", "217880", "076340", "217320", "216280", "215050", "176750", "208890", "210120", "150440", "149010", "121060", "202960", "136660", "199150", "084440", "140660", "189350", "178600", "140290", "107640", "183410", "185190", "114920", "179720", "086460", "116100"};
		HashMap<String, Integer> mapci=new HashMap<>();
		HashMap<Integer, String> mapic=new HashMap<>();
		int idx=0;
		int Y=2020;
		int EY=Y+1;
		for (int i=0; i<kospi_code.length; i++) {
			mapic.put(idx, kospi_code[i]);
			mapci.put(kospi_code[i], idx++);
		}
		for (int i=0; i<kosdaq_code.length; i++) {
			mapic.put(idx, kosdaq_code[i]);
			mapci.put(kosdaq_code[i], idx++);
		}
		for (int i=0; i<konex_code.length; i++) {
			mapic.put(idx, konex_code[i]);
			mapci.put(konex_code[i], idx++);
		}
		
//		for (String key: mapci.keySet()) {
//			System.out.println(key+" "+mapci.get(key));
//		}
//		
//		System.out.println(idx);
		int n=10000;
		int count=2500;
		String[][][] total=new String[count][n][];
		int[] index=new int[count];
		String[][][] arr=new String[count][n][12];
//		int weeks=0;
		for (int y=Y; y<=EY; y++) {
			System.out.println(y);
			System.setIn(new FileInputStream("C:/ssafy/project2/data/marcap/data/marcap-"+y+".csv/marcap-"+y+".csv"));
			br=new BufferedReader(new InputStreamReader(System.in));
			br.readLine();
			while(true) {
				String str=br.readLine();
				if (str==null) {
					break;
				}
				String code=str.split(",")[0].substring(1, 7);
				if (!mapci.containsKey(code)) {
					continue;
				}
				int codeIdx=mapci.get(code);
				total[codeIdx][index[codeIdx]++]=str.split(",");
//				System.out.println(index[codeIdx]);
			}
		}
		
	
		System.out.println("input end");
		
		int[] weekIndex=new int[count];
//		System.out.println(Arrays.toString(index));
//		"code_number","Name","Market","Dept","Close",	// 0~4
//		"ChangeCode","Changes","ChagesRatio","Open","High","Low",	// 5~10
//		"Volume","Amount","Marcap","Stocks","MarketId","Rank","Date"	// 11~17
//		`code_number`,`current_price`,`changes`,`chages_ratio`,`start_price`,`high_price`,
//		`low_price`, `volume`,`trade_price`,`market_cap`,`stock_amount`,`date`
		for (int i=0; i<count; i++) {
			int beforeDate=10;
			String weekDate=null;
			int start=0;
			int end=0;
			int high=0;
			int low=10000000;
			long volume=0l;
			long tradePrice=0l;
			long marketCap=0l;
			long stockAmount=0l;
			int changes=0;
			double changeRatio=0.0;
			int k=0;
			for (int j=0; j<index[i]; j++) {
				String[] cur=total[i][j];
				String dateStr=cur[17].split("\"")[1];
				int year=Integer.parseInt(dateStr.split("-")[0]);
				int month=Integer.parseInt(dateStr.split("-")[1]);
				int day=Integer.parseInt(dateStr.split("-")[2]);
				LocalDate date = LocalDate.of(year, month, day);
				int curDate = date.getDayOfWeek().getValue();
				
				if (beforeDate>curDate) {	// 일주일의 시작
					arr[i][k][0]=cur[0];
					arr[i][k][1]=end+"";
					arr[i][k][2]=changes+"";
					arr[i][k][3]=changeRatio+"";
					arr[i][k][4]=start+"";
					arr[i][k][5]=high+"";
					arr[i][k][6]=low+"";
					arr[i][k][7]=volume+"";
					arr[i][k][8]=tradePrice+"";
					arr[i][k][9]=marketCap+"";
					arr[i][k][10]=stockAmount+"";
					arr[i][k][11]=weekDate;
					k++;
					
//					System.out.println(cur[0]);
					start=(int)Double.parseDouble(cur[8]);
					high=(int)Double.parseDouble(cur[9]);
					low=(int)Double.parseDouble(cur[10]);
					volume=(int)Double.parseDouble(cur[11]);
					tradePrice=(long)Double.parseDouble(cur[12]);
				} else {
					high=Math.max(high, (int)Double.parseDouble(cur[9]));
					low=Math.min(low, (int)Double.parseDouble(cur[10]));
					volume+=(long)Double.parseDouble(cur[11]);
					tradePrice+=(long)Double.parseDouble(cur[12]);
				}
				end=(int)Double.parseDouble(cur[4]);	// 일주일의 마지막 날로 최종 업데이트
				marketCap=(long)Double.parseDouble(cur[13]);
				stockAmount=(long)Double.parseDouble(cur[14]);
				weekDate=cur[17];
				int beforeEnd=Integer.parseInt(arr[i][k-1][1]);
				if (beforeEnd==0) {
					beforeEnd=end;
				}
				changes=end-beforeEnd;
				changeRatio=(int)(10000*changes/beforeEnd)/100.0;
				
				beforeDate=curDate;
			}
			
			weekIndex[i]=k;
//			System.out.println(Arrays.toString(total[i][i]));
//			break;
			
		}
		
		System.out.println("save");
		int k=0;
		for (int i=0; i<count; i++) {
			for (int j=0; j<weekIndex[i]; j++) {
				if (arr[i][j][0]==null || j==0) {
					continue;
				}
				if (!"\"2021-01-15\"".equals(arr[i][j][11])) {
					continue;
				}
				
				if (k%3640==0) {
					sb.append("insert into week_stock (`code_number`,`current_price`,`changes`,`chages_ratio`,`start_price`,`high_price`,`low_price`,`volume`,`trade_price`,`market_cap`,`stock_amount`,`date`) VALUES ");
				}
				sb.append("(")
				.append(arr[i][j][0]).append(",")
				.append(arr[i][j][1]).append(",")
				.append(arr[i][j][2]).append(",")
				.append(arr[i][j][3]).append(",")
				.append(arr[i][j][4]).append(",")
				.append(arr[i][j][5]).append(",")
				.append(arr[i][j][6]).append(",")
				.append(arr[i][j][7]).append(",")
				.append(arr[i][j][8]).append(",")
				.append(arr[i][j][9]).append(",")
				.append(arr[i][j][10]).append(",")
				.append(arr[i][j][11]).append(")");
				if (k%3640==3639) {
					sb.append(";\n");
				} else {
					sb.append(",\n");
				}
				k++;
			}
		}
		
		String result=sb.substring(0, sb.length()-2)+";";
//		System.out.println(result);
		BufferedOutputStream bs = null;
		try {
//			bs = new BufferedOutputStream(new FileOutputStream("C:/ssafy/project2/data/day_stock/week_stock"+Y+".sql"));
			bs = new BufferedOutputStream(new FileOutputStream("C:/ssafy/project2/data/day_stock/20210115.sql"));
			bs.write(result.getBytes()); //Byte형으로만 넣을 수 있음

		} catch (Exception e) {
	                e.getStackTrace();
			// TODO: handle exception
		}finally {
			bs.close();
		}
		
		System.out.println("end");
		
	}

}