SELECT * FROM movie WHERE name LIKE '%programmer%' ORDER BY id;

SELECT m.id as mid, d.*
FROM MOVIE m, MOVIE_DIRECTORS md, DIRECTORS d
WHERE m.id = md.mid AND d.id = md.did AND m.name LIKE '%programmer%'
ORDER BY mid;


SELECT m.id as mid, d.*
FROM 
MOVIE m LEFT JOIN MOVIE_DIRECTORS md ON m.id = md.mid
			   LEFT JOIN DIRECTORS d ON d.id = md.did
WHERE m.name LIKE '%programmer%'
ORDER BY mid;

SELECT m.id as movie_id, a.*
FROM 
ACTOR a FULL JOIN CASTS c ON a.id = c.pid
			   FULL JOIN MOVIE m ON m.id = c.mid
WHERE m.name LIKE '%programmer%'
ORDER BY movie_id;


/*id          name                   year
----------  ---------------------  ----------
596214      The Lonely Programmer  2005
777191      Battle Programmer Shi  2003
1080045     The Programmer
1298880     Programmer/Child's Pl
1386180     Programmer
1454511     You Can't Tell the Pl  1968
1475341     The Deprogrammers      1996
1544875     Spy on Your Spouse/Ha
*/

/*
mid         id          fname       lname
----------  ----------  ----------  ----------
596214      113737       Alex       Mechlin
777191      72020        Hiroki     Hayashi
1080045
1298880
1386180     89911        Guzel      Kireeva
1454511     13094        Earl       Bellamy
1475341     153364       Joseph L.  Scanlan
1544875
/*

/*movie_id    id          fname       lname       gender
----------  ----------  ----------  ----------  ------
777191      438873      Takaya      Hashi       M
777191      487394      Hajime      Iijima      M
777191      751815      Kazuya      Nakai       M
777191      1034255     Hiroki      Takahashi   M
777191      1034644     Yasuhiro    Takato      M
777191      1158324     Hiroshi     Yanaka      M
777191      1385316     Misato      Fukuen      F
777191      1511505     Kujira                  F
777191      1641940     Fumiko      Orikasa     F
777191      1784277     Atsuko      Tanaka      F
1080045     27471       Erich       Anderson    M
1080045     78289       Noah        Beggs       M
1080045     98797       Tim         Bissett     M
1080045     330930      Fab         Filippo     M
1080045     540576      Michael     Kelly       M
1080045     652496      Romany      Malco       M
1080045     670194      Max         Martini     M
1080045     737288      Jesse       Moss        M
1080045     845103      Esteban     Powell      M
1080045     898887      Benjamin    Rogers      M
1080045     959760      Jeff        Seymour     M
1080045     1247431     Lynda       Boyd        F
1080045     1354729     Beverley    Elliott     F
1080045     1446202     Kate        Hodge       F
1080045     1615030     Kim         Murphy      F
1080045     1651105     Susie       Park        F
1298880     125701      John H.     Brennan     M
1298880     172902      Tom         Cavanagh    M
1298880     341361      Steven      Ford        M
1298880     794126      Peter       Outerbridg  M
1386180     973168      Alexander   Siguev      M
1386180     1169967     Petar       Zekavica    M
1386180     1241739     Anna        Bolshova    F
1386180     1394094     Lyudmila    Gavrilova   F
1386180     1765903     Natalya     Starykh     F
1454511     30048       Tige        Andrews     M
1454511     69684       Harry       Basch       M
1454511     202396      Michael     Cole        M
1454511     344117      Byron       Foulger     M
1454511     386456      Mark        Goddard     M
1454511     433878      Jerry       Harper      M
1454511     705442      Art         Metrano     M
1454511     1138472     Clarence    Williams I  M
1454511     1180916     Julie       Adams       F
1454511     1540634     Peggy       Lipton      F
1454511     1545783     Sarah       Lord        F
1454511     1569075     Linda       Marsh       F
1454511     1831793     Dodie       Warren      F
1475341     27471       Erich       Anderson    M
1475341     209343      Kevin       Conway      M
1475341     427543      Vincent     Hammond     M
1475341     427543      Vincent     Hammond     M
1475341     434326      Adam        Harrington  M
1475341     691746      Dean        McKenzie    M
1475341     1000466     Brent       Spiner      M
1475341     1102080     Sam         Vincent     M
1475341     1639718     Nicole      Oliver      F
1544875     103003      Mike        Blaylock    M
1544875     593740      John        LaSage      M
1544875     1093133     Ryoga       Vee         M*/