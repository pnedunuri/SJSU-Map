package edu.sjsu.cmpe277.org.sjsumap;

/**
 * Created by pnedunuri on 10/21/16.
 */

public class Constants {
    public static final int DEFAULT_DPI = 160;
    public static final double ACTUAL_ASPECT_RATIO = 1.661;

    // BUILDING INDEXES
    public static final int NP_GARAGE = 0;
    public static final int KING_LIB = NP_GARAGE + 1;
    public static final int UT_N_HGH = KING_LIB + 1;
    public static final int DMH = UT_N_HGH + 1;
    public static final int IRC = DMH + 1;
    public static final int CAR = IRC + 1;
    public static final int ADM = CAR + 1;
    public static final int CC = ADM + 1;
    public static final int CL = CC + 1;
    public static final int ENG = CL + 1;
    public static final int IS = ENG + 1;
    public static final int CYA = IS + 1;
    public static final int CYB = CYA + 1;
    public static final int ATM = CYB + 1;
    public static final int SCI = ATM + 1;
    public static final int WSQ = SCI + 1;
    public static final int TH_N_MD = WSQ + 1;
    public static final int DBH = TH_N_MD + 1;
    public static final int CCB = DBH + 1;
    public static final int SU = CCB + 1;
    public static final int BK = SU + 1;
    public static final int MUS_N_CH = BK + 1;
    public static final int ART = MUS_N_CH + 1;
    public static final int BT = ART + 1;
    public static final int BBC = BT + 1;
    public static final int YUH = BBC + 1;
    public static final int SPM = YUH + 1;
    public static final int FOB = SPM + 1;
    public static final int SPXC = FOB + 1;
    public static final int SPXE = SPXC + 1;
    public static final int SHCC = SPXE + 1;
    public static final int EC = SHCC + 1;
    public static final int UNKOWN = EC + 1;
    public static final int CP = UNKOWN + 1;
    public static final int WP_GARAGE = CP + 1;
    public static final int ASH = WP_GARAGE + 1;
    public static final int DH = ASH + 1;
    public static final int MQH = DH + 1;
    public static final int SH = MQH + 1;
    public static final int SP_GARAGE = SH + 1;
    public static final int UPD = SP_GARAGE + 1;
    public static final int B_N_A = UPD + 1;
    public static final int ASP_N_HOV = B_N_A + 1;
    public static final int RYC = ASP_N_HOV + 1;
    public static final int WSH = RYC + 1;
    public static final int AQX = WSH + 1;
    public static final int DC = AQX + 1;
    public static final int BB = DC + 1;
    public static final int JWH = BB + 1;
    public static final int CVC = JWH + 1;
    public static final int CVA = CVC + 1;
    public static final int CVB = CVA + 1;
    public static final int NO_OF_BUILDINGS = CVB + 1;

    // COORDINATE INDEXES
    public static final byte LEFT_INDEX = 0;
    public static final byte TOP_INDEX = LEFT_INDEX + 1;
    public static final byte RIGHT_INDEX = TOP_INDEX + 1;
    public static final byte DOWN_INDEX = RIGHT_INDEX + 1;
    public static final byte NUM_DIR_INDEXES = DOWN_INDEX + 1;

    public static final int[] MAP_RESOLUTION = {
            0, 0, 607, 654
    };

    public static final int[][] MAP_BUILDINGS_COORDS = {
            {
                    530, 27, 580, 125
            },
            {
                    56, 172, 112, 255
            },
            {
                    130, 174, 166, 218
            },
            {
                    192, 172, 234, 190
            },
            {
                    192, 204, 232, 224
            },
            {
                    260, 172, 295, 193
            },
            {
                    261, 203, 294, 220
            },
            {
                    205, 243, 228, 255
            },
            {
                    250, 240, 300, 272
            },
            {
                    340, 180, 408, 250
            },
            {
                    450, 181, 470, 250
            },
            {
                    530, 173, 545, 195
            },
            {
                    545, 210, 580, 235
            },
            {
                    535, 263, 560, 290
            },
            {
                    62, 266, 75, 308
            },
            {
                    65, 355, 110, 365
            },
            {
                    164, 292, 213, 327
            },
            {
                    187, 344, 244, 368
            },
            {
                    290, 303, 301, 375
            },
            {
                    326, 286, 418, 332
            },
            {
                    440, 285, 488, 331
            },
            {
                    332, 346, 400, 385
            },
            {
                    416, 348, 480, 387
            },
            {
                    555, 304, 587, 382
            },
            {
                    535, 358, 585, 380
            },
            {
                    43, 390, 100, 444
            },
            {
                    118, 378, 147, 408
            },
            {
                    168, 390, 222, 402
            },
            {
                    122, 414, 190, 447
            },
            {
                    207, 416, 262, 449
            },
            {
                    285, 403, 302, 448
            },
            {
                    328, 403, 436, 453
            },
            {
                    464, 406, 490, 460
            },
            {
                    533, 400, 574, 458
            },
            {
                    43, 485, 69, 609
            },
            {
                    105, 478, 136, 516
            },
            {
                    84, 543, 152, 611
            },
            {
                    177, 485, 224, 532
            },
            {
                    243, 481, 298, 527
            },
            {
                    190, 553, 260, 608
            },
            {
                    290, 585, 301, 607
            },
            {
                    373, 490, 394, 494
            },
            {
                    326, 505, 390, 528
            },
            {
                    320, 538, 388, 569
            },
            {
                    325, 585, 392, 628
            },
            {
                    422, 474, 450, 544
            },
            {
                    422, 570, 447, 611
            },
            {
                    480, 500, 492, 533
            },
            {
                    480, 572, 490, 614
            },
            {
                    525, 479, 544, 508
            },
            {
                    525, 568, 547, 617
            },
            {
                    570, 517, 587, 553
            }
    };

    // LATITUDE, LONGITUDE INDEXES
    public static final byte LATITUDE_INDEX = 0;
    public static final byte LONGITUDE_INDEX = LATITUDE_INDEX + 1;

    // LATITUDE LONGITUDE
    public static final double[][] LATITUDE_LONGITUDE = {
            {
                    37.339458, -121.880638
            },
            {
                    37.335775, -121.885310
            },
            {
                    37.3358133, -121.8842735
            },
            {
                    37.336262, -121.8840542
            },
            {
                    37.336262, -121.8840542
            },
            {
                    37.3365174, -121.883104
            },
            {
                    37.3365174, -121.883104
            },
            {
                    37.3359359, -121.8833193
            },
            {
                    37.3359148,-121.8825466
            },
            {
                    37.33720651,-121.88245979
            },
            {
                    37.337593, -121.880605
            },
            {
                    37.338244, -121.880353
            },
            {
                    37.337763, -121.879498
            },
            {
                    37.337312, -121.879771
            },
            {
                    37.334744, -121.884713
            },
            {
                    37.334249, -121.884137
            },
            {
                    37.335332, -121.883232
            },
            {
                    37.335160, -121.882344
            },
            {
                    37.335569, -121.881874
            },
            {
                    37.336333, -121.881294
            },
            {
                    37.336917, -121.880267
            },
            {
                    37.335472, -121.880905
            },
            {
                    37.335973, -121.879725
            },
            {
                    37.337074, -121.878947
            },
            {
                    37.33664351, -121.87839356
            },
            {
                    37.333764, -121.883972
            },
            {
                    37.334326, -121.883394
            },
            {
                    37.334643, -121.882558
            },
            {
                    37.334195, -121.882433
            },
            {
                    37.334570, -121.881689
            },
            {
                    37.334774, -121.881179
            },
            {
                    37.335393, -121.880094
            },
            {
                    37.335788, -121.879173
            },
            {
                    37.336213, -121.878392
            },
            {
                    37.332575, -121.883055
            },
            {
                    37.333319, -121.882724
            },
            {
                    37.332746, -121.882217
            },
            {
                    37.333436, -121.881726
            },
            {
                    37.333922, -121.880929
            },
            {
                    37.33277335, -121.88060029
            },
            {
                    37.333138, -121.879894
            },
            {

            },
            {
                    37.334448, -121.879892
            },
            {
                    37.334116, -121.879667
            },
            {
                    37.333691, -121.879350
            },
            {
                    37.334765, -121.878935
            },
            {
                    37.334070, -121.878513
            },
            {
                    37.335284, -121.878597
            },
            {
                    37.334290, -121.878068
            },
            {
                    37.335742, -121.878020
            },
            {
                    37.334568, -121.877585
            },
            {
                    37.334988, -121.876864
            }

    };

    // COLOR INDEXES
    public static final byte RED_COLOR_INDEX = 0;
    public static final byte GREEN_COLOR_INDEX = RED_COLOR_INDEX + 1;
    public static final byte BLUE_COLOR_INDEX = GREEN_COLOR_INDEX + 1;
    public static final byte NUM_COLOR_INDEXES = BLUE_COLOR_INDEX + 1;


    // COLOR VALUES FOR BUILDINGS
    public static final int [][] BUILDINGS_COLOR_VALUES = {
            {
                    254, 0, 0
            },
            {
                    0, 255, 1
            },
            {
                    0, 0, 254
            },
            {
                    127, 0, 55
            },
            {
                    255, 0, 255
            },
            {
                    255, 127, 126
            },
            {
                    127, 50, 0
            },
            {
                    0, 125, 125
            },
            {
                    255, 127, 185
            },
            {
                    255, 230, 125
            },
            {
                    127, 106, 0
            },
            {
                    0, 127, 12
            },
            {
                    255, 127, 240
            },
            {
                    255, 0, 110
            },
            {
                    0, 255, 255
            },
            {
                    113, 113, 113
            },
            {
                    10, 170, 20
            },
            {
                    86, 0, 127
            },
            {
                    50, 100, 127
            },
            {
                    128, 201, 255
            },
            {
                    1, 74, 127
            },
            {
                    164, 255, 127
            },
            {
                    214, 225, 215
            },
            {
                    179, 0, 255
            },
            {
                    153, 245, 153
            },
            {
                    255, 217, 0
            },
            {
                    160, 160, 160
            },
            {
                    127, 255, 200
            },
            {
                    214, 154, 243
            },
            {
                    255, 105, 0
            },
            {
                    127, 127, 255
            },
            {
                    100, 127, 170
            },
            {
                    0, 180, 255
            },
            {
                    85, 8, 55
            },
            {
                    218, 221, 174
            },
            {
                    187, 114, 131
            },
            {
                    0, 255, 145
            },
            {
                    223, 188, 96
            },
            {
                    250, 235, 214
            },
            {
                    10, 28, 100
            },
            {
                    215, 255, 125
            },
            {
                    42, 65, 55
            },
            {
                    170, 98, 46
            },
            {
                    127, 0, 112
            },
            {
                    92, 40, 3
            },
            {
                    195, 218, 136
            },
            {
                    103, 142, 124
            },
            {
                    3, 56, 92
            },
            {
                    2, 80, 80
            },
            {
                    104, 149, 85
            },
            {
                    161, 92, 92
            },
            {
                    122, 158, 50
            }
    };

    public static final String[] BUILDING_NAMES = {
            "NP_GARAGE_BUILDING",
            "King Library",
            "UT_N_HGH_BUILDING",
            "DMH_BUILDING",
            "IRC_BUILDING",
            "CAR_BUILDING",
            "ADM_BUILDING",
            "CC_BUILDING",
            "CL_BUILDING",
            "Engineering Building",
            "IS_BUILDING",
            "CYA_BUILDING",
            "CYB_BUILDING",
            "ATM_BUILDING",
            "SCI_BUILDING",
            "WSQ_BUILDING",
            "TH_N_MD_BUILDING",
            "DBH_BUILDING",
            "CCB_BUILDING",
            "Student Union",
            "BK_BUILDING",
            "MUS_N_CH_BUILDING",
            "ART_BUILDING",
            "BT_BUILDING",
            "BBC",
            "Yoshihiro Uchida Hall",
            "SPM_BUILDING",
            "FOB_BUILDING",
            "SPXC_BUILDING",
            "SPXE_BUILDING",
            "SHCC_BUILDING",
            "EC_BUILDING",
            "UNKOWN_BUILDING",
            "CP_BUILDING",
            "WP_GARAGE_BUILDING",
            "ASH_BUILDING",
            "DH_BUILDING",
            "MQH_BUILDING",
            "SH_BUILDING",
            "South Parking Garage",
            "UPD_BUILDING",
            "B_N_A_BUILDING",
            "ASP_N_HOV_BUILDING",
            "RYC_BUILDING",
            "WSH_BUILDING",
            "AQX_BUILDING",
            "DC_BUILDING",
            "BB_BUILDING",
            "JWH_BUILDING",
            "CVC_BUILDING",
            "CVA_BUILDING",
            "CVB_BUILDING"
    };

    //Building Addresses
    public static final String[] BUILDING_ADDRESSES = {
            "",
            "Dr. Martin Luther King, Jr. Library, 150 East San Fernando Street, San Jose, CA 95112",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            " San Jose State University Charles W. Davidson College of Engineering, 1 Washington Square, San Jose, CA 95112",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "San Jose State University, 211 S 9th St, San Jose, CA 95112",
            "",
            "",
            "",
            "",
            "Boccardo Business Complex, San Jose, CA 95112",
            "Yoshihiro Uchida Hall, San Jose, CA 95112",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            " San Jose State University South Garage, 330 South 7th Street, San Jose, CA 95112",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    //Building Addresses
    public static final String[] BUILDING_IMAGE_NAMES = {
            "",
            "king_library",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "engineering_building",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "student_union",
            "",
            "",
            "",
            "",
            "bbc",
            "yoshihiro_uchida_hall",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "south_parking_garage",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    // Building Co-Ordinates Original
    public static final double BUILDING_COORDINATES_POTRAIT[][] = {
            {
                    227.31063213785154, 16.871730262852896, 256.2253145988958, 81.90512178125948
            },
            {
                    21.89331153453932, 104.19418726706047, 51.80535193868991, 155.39612315368933
            },
            {
                    51.80535193868991, 105.19734380285924, 73.89581562477349, 133.30674670373557
            },
            {
                    81.92940338815923, 106.20086275070132, 103.01417365414108, 114.2431484031258
            },
            {
                    81.92940338815923, 124.27652582133008, 103.01417365414108, 137.3200976710173
            },
            {
                    112.0530924255853, 105.19734380285924, 131.12756308749348, 104.19418726706047
            },
            {
                    114.05505655266352, 123.27336928553127, 132.13289409555196, 135.31342218737643
            },
            {
                    86.9469981273696, 147.35347508922158, 101.00351163802414, 156.39927968948808
            },
            {
                    105.02483567025801, 144.34364306978193, 136.9479005025688, 170.44600807497412
            },
            {
                    141.97346830673132, 105.19734380285924, 181.12810305064946, 165.4295005718936
            },
            {
                    187.15102879791823, 109.22627848800201, 213.33566133693498, 161.4161496046119
            },
            {
                    224.30333700271487, 103.19084952524004, 235.14126915700055, 123.27336928553127
            },
            {
                    225.29997012173462, 128.28987678861176, 255.2207084149239, 137.3200976710173
            },
            {
                    225.29997012173462, 155.39612315368933, 242.16974335955376, 181.48290444102048
            },
            {
                    24.900570428471674, 158.40595517312894, 47.79308820753803, 196.5487354922096
            },
            {
                    25.905212853647914, 196.5487354922096, 50.80908123171344, 229.65927962553133
            },
            {
                    71.8941414273299, 177.46955347373876, 91.97365316766194, 198.5554109758504
            },
            {
                    79.73064952158003, 211.59898282553766, 108.72361298397277, 227.65238669466453
            },
            {
                    116.06571856878045, 184.49309162426252, 132.13302093976708, 229.65927962553133
            },
            {
                    139.9638137960947, 174.45965984425175, 176.11067502097902, 196.5489166982312
            },
            {
                    183.1389897622332, 173.45632210243133, 214.25930467043815, 203.57231713217854
            },
            {
                    148.99390783428507, 210.59571756612587, 175.10536938176358, 236.68269093183994
            },
            {
                    182.1336877471382, 210.59571756612587, 213.25400990358403, 240.7117597094387
            },
            {
                    236.14672700927417, 187.50313021856684, 255.2210490822446, 198.55559218187204
            },
            {
                    226.3053373709974, 206.58236659884415, 254.21573981890873, 232.66932909219696
            },
            {
                    19.882877838009648, 234.67600457583782, 48.79860042161813, 269.80873542825276
            },
            {
                    49.803906060833576, 234.67600457583782, 60.8446113774548, 247.73516014338605
            },
            {
                    70.88883578811446, 233.67266683401738, 99.99856116261111, 241.71509745125914
            },
            {
                    52.810998245226024, 254.75856057733338, 83.94014875904614, 274.8254603785593
            },
            {
                    88.95785222186946, 252.75189596605384, 116.06579105118911, 274.8254603785593
            },
            {
                    118.0763950813791, 244.72512517320217, 130.12241328545664, 273.82212263673887
            },
            {
                    141.97442145040515, 245.72845929090212, 194.17968420649314, 277.83547360402054
            },
            {
                    201.20801344422904, 248.73849788520647, 212.24870064024813, 272.8187595260753
            },
            {
                    224.3035544499408, 243.72179105550217, 250.20335649178259, 280.8611937674376
            },
            {
                    18.87757328603034, 292.90133002405264, 30.923586778751307, 382.23032841944513
            },
            {
                    46.78799276730769, 294.90801638005485, 55.826911538751915, 305.9448221430904
            },
            {
                    35.95010772658763, 331.0441212154946, 66.86762047949357, 377.21363971034293
            },
            {
                    77.91713965700737, 296.9147136084183, 94.98085407566737, 323.00169059825276
            },
            {
                    104.01977647123202, 295.9113541218753, 131.12771892467208, 328.0340862453107
            },
            {
                    80.92423908964068, 334.0541344409559, 119.08190729545923, 378.21701369336773
            },
            {
                    119.08169709647412, 360.1410824378269, 133.9408010699355, 377.21363971034293
            },
            {
                    157.027539087116, 293.9046786382344, 169.08236027972478, 301.9314385587248
            },
            {
                    139.9638137960947, 303.93812853884737, 172.08945971235812, 326.0273600239838
            },
            {
                    137.9532025176638, 327.0307340070085, 169.08236027972478, 354.12103424218174
            },
            {
                    141.97442145040515, 353.11766025915705, 169.08236027972478, 379.2203151939838
            },
            {
                    180.12307646870735, 292.90133002405264, 198.19209290246232, 333.05078220265375
            },
            {
                    177.10716317518146, 345.0909220833893, 197.18679813560817, 374.2035902436773
            },
            {
                    202.21330821108322, 306.9481598849108, 217.2752252122049, 317.98492940674197
            },
            {
                    200.20270418089322, 351.11099927199785, 214.25930467043815, 376.2103019685225
            },
            {
                    220.28233914131994, 295.9113541218753, 243.17505262288967, 316.98162790612594
            },
            {
                    218.2717133664073, 348.10097155005485, 243.17505262288967, 377.21363971034293
            },
            {
                    238.15733466358463, 309.9581912309742, 258.23695512752965, 376.2103019685225
            }
    };

    // API KEY is embedded to the url
    // replace usrLoc with user Latitude and Logitude
    // similarly replace destLoc with the requested destination location
    public static final String MAPS_API_REQUEST_URI = "https://maps.googleapis.com/maps/api/distancematrix/json";
    public static final String MAPS_API_REQUEST_QUERY = "mode=walking&units=imperial&origins=usrLoc&destinations=destLoc&key=AIzaSyCSaSsWaBVTYpDLZxa7kcwl_rhRJg8xRJI";
}
