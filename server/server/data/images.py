import random

images = [
    'https://images.unsplash.com/photo-1462524500090-89443873e2b4',
    'https://images.unsplash.com/12/green.jpg',
    'https://images.unsplash.com/photo-1475260231698-62391846c9d5',
    'https://images.unsplash.com/reserve/qstJZUtQ4uAjijbpLzbT_LO234824.JPG',
    'https://images.unsplash.com/photo-1427466920301-a96b3cadc6c8',
    'https://images.unsplash.com/photo-1451807278147-2e0e1e90b24d',
    'https://images.unsplash.com/photo-1431631927486-6603c868ce5e',
    'https://images.unsplash.com/photo-1428190318100-06790c8b2e5a',
    'https://images.unsplash.com/photo-1475738079182-05ab3f90239b',
    'https://images.unsplash.com/16/unsplash_5263607dd1bfc_2.jpg',
    'https://images.unsplash.com/photo-1432256851563-20155d0b7a39',
    'https://images.unsplash.com/photo-1422651355218-53453822ebb8',
    'https://images.unsplash.com/photo-1433190152045-5a94184895da',
    'https://images.unsplash.com/photo-1427955569621-3e494de2b1d2',
    'https://images.unsplash.com/photo-1447522200268-a0378dac3fba',
    'https://images.unsplash.com/photo-1475260231698-62391846c9d5',
    'https://images.unsplash.com/photo-1451443700141-5ddb6d85a8fc',
    'https://images.unsplash.com/photo-1460661419201-fd4cecdf8a8b',
    'https://images.unsplash.com/photo-1478754117940-f5bc84311001',
    'https://images.unsplash.com/photo-1476862921040-227a643bf014',
    'https://images.unsplash.com/photo-1477414956199-7dafc86a4f1a',
    'https://images.unsplash.com/photo-1480354220088-fc118301a98d',
]

people = [
    'https://images.unsplash.com/photo-1477966118833-41e30282b4a4',
    'https://images.unsplash.com/photo-1478031813121-5f718ad4a15f',
    'https://images.unsplash.com/photo-1476722877629-c40939e0fbd7',
    'https://images.unsplash.com/photo-1476459216969-94c7100f88a2',
    'https://images.unsplash.com/photo-1476902287700-4791d15e8aa3',
    'https://images.unsplash.com/photo-1476365365201-dce7deb37bf2',
    'https://images.unsplash.com/photo-1476400282261-9e43f8bf74e6',
    'https://images.unsplash.com/photo-1464746133101-a2c3f88e0dd9',
    'https://images.unsplash.com/photo-1465821185615-20b3c2fbf41b',
    'https://images.unsplash.com/photo-1476297587631-7c59d0c57a02',
    'https://images.unsplash.com/photo-1476055090065-a605fefd840e',
    'https://images.unsplash.com/photo-1476101751557-bbe4d57684e9',
    'https://images.unsplash.com/photo-1476119076329-532a3ff43696',
    'https://images.unsplash.com/photo-1476231790875-016a80c274f3',
    'https://images.unsplash.com/photo-1475869568365-7b6051b1e030',
    'https://images.unsplash.com/photo-1475870434835-a633fd526088',
    'https://images.unsplash.com/photo-1472739841375-d0ea9f0cb6a6',
    'https://images.unsplash.com/photo-1447871852826-1c11ef907903',
    'https://images.unsplash.com/photo-1475595828126-ebeaf5e7b22e',
    'https://images.unsplash.com/photo-1476036896437-6a24b25e6e50',
    'https://images.unsplash.com/photo-1467911924913-a48e954914e1',
    'https://images.unsplash.com/photo-1438978849647-a3503a63d760',

]

christmas = [
    'https://images.unsplash.com/photo-1448006678469-d32ba7880e18',
    'https://images.unsplash.com/photo-1450258293730-59d0e9340295',
    'https://images.unsplash.com/photo-1481089584114-3f7354151e8a',
    'https://images.unsplash.com/photo-1480460673325-52897ac06af0',
    'https://images.unsplash.com/photo-1446814699583-d835df35a267',
    'https://images.unsplash.com/photo-1478293888741-aee4356f71c7',
    'https://images.unsplash.com/photo-1481481525014-91e77115eace',
    'https://images.unsplash.com/photo-1480413258216-ff003d179d65',
    'https://images.unsplash.com/photo-1449960132599-0ab49fff5d9a',
    'https://images.unsplash.com/photo-1481097618128-d40d460be582',
    'https://images.unsplash.com/photo-1480618757544-81c31930008e',
    'https://images.unsplash.com/photo-1480717385037-735ebeb9a198'
]

sports = [
    'https://images.unsplash.com/photo-1418846531910-2b7bb1043512',
    'https://images.unsplash.com/photo-1469155472021-fb3489e556fb',
    'https://images.unsplash.com/photo-1417716226287-2f8cd2e80274',
    'https://images.unsplash.com/photo-1480699132165-c222f12b6c66',
    'https://images.unsplash.com/photo-1473682150760-51d4f94b09d4',
    'https://images.unsplash.com/photo-1446057633965-55d2dcb22598',
    'https://images.unsplash.com/photo-1480457974161-db9c6e5eb335',
    'https://images.unsplash.com/photo-1420393000485-4697383da9ec',
    'https://images.unsplash.com/photo-1444491741275-3747c53c99b4',
    'https://images.unsplash.com/photo-1473282082880-a8c6d914e456',
    'https://images.unsplash.com/photo-1481078906426-9bf95a7ec345',
    'https://images.unsplash.com/photo-1444824318033-1e9b6797d69d',
    'https://images.unsplash.com/photo-1455657255576-9f8594457f6b',
    'https://images.unsplash.com/photo-1453694595360-51e193e121fc',
]

winter = [
    'https://images.unsplash.com/photo-1452850387937-f79e4c063cf2',
    'https://images.unsplash.com/photo-1452520059272-76d6d0a43dfd',
]

candidate_2 = [

]

candidates_1 = [
    'https://images.unsplash.com/photo-1447522200268-a0378dac3fba',
    'https://images.unsplash.com/photo-1444842741774-771cf4455a78',
    'https://images.unsplash.com/photo-1427955569621-3e494de2b1d2',
    'https://images.unsplash.com/photo-1433190152045-5a94184895da',
    'https://images.unsplash.com/photo-1451667625187-c7d38e2fee3c',
    'https://images.unsplash.com/photo-1469474968028-56623f02e42e',
    'https://images.unsplash.com/photo-1422651355218-53453822ebb8',
    'https://images.unsplash.com/photo-1471931452361-f5ff1faa15ad',
    'https://images.unsplash.com/photo-1429979787503-f2d7d20550c8',
    'https://images.unsplash.com/39/wdXqHcTwSTmLuKOGz92L_Landscape.jpg',
    'https://images.unsplash.com/photo-1452215199360-c16ba37005fe',
    'https://images.unsplash.com/photo-1470770903676-69b98201ea1c',
    'https://images.unsplash.com/photo-1471882243907-ba1cf91b79ef',
    'https://images.unsplash.com/23/pink-sky.JPG',
    'https://images.unsplash.com/photo-1466220666686-90bdba318c9a',
    'https://images.unsplash.com/photo-1432256851563-20155d0b7a39',
    'https://images.unsplash.com/16/unsplash_5263607dd1bfc_2.jpg',
    'https://images.unsplash.com/photo-1473283147055-e39c51463929',
    'https://images.unsplash.com/photo-1434907652076-85f8401482c3',
    'https://images.unsplash.com/photo-1464400694175-33544b41703d',
    'https://images.unsplash.com/photo-1475738079182-05ab3f90239b',
    'https://images.unsplash.com/photo-1476820865390-c52aeebb9891',
    'https://images.unsplash.com/photo-1428190318100-06790c8b2e5a',
    'https://images.unsplash.com/reserve/sW4FHZKlQ4qQLNTy3yS0__IMG0278.JPG',
    'https://images.unsplash.com/photo-1431631927486-6603c868ce5e',
    'https://images.unsplash.com/photo-1423766111988-c47a5ff6ed06',
    'https://images.unsplash.com/photo-1472718357940-bdf5a0aa0a42',
    'https://images.unsplash.com/photo-1451807278147-2e0e1e90b24d',
    'https://images.unsplash.com/photo-1431057499046-ecd6e0f36ebe',
    'https://images.unsplash.com/photo-1429152937938-07b5f2828cdd',
    'https://images.unsplash.com/photo-1471347334704-25603ca7d537',
    'https://images.unsplash.com/photo-1427466920301-a96b3cadc6c8',
    'https://images.unsplash.com/reserve/qstJZUtQ4uAjijbpLzbT_LO234824.JPG',
    'https://images.unsplash.com/reserve/OnRKhvlFQ2uJNSx5O3cc_DSC00560.jpg',
    'https://images.unsplash.com/photo-1439396087961-98bc12c21176',
    'https://images.unsplash.com/photo-1452850387937-f79e4c063cf2',
    'https://images.unsplash.com/photo-1450740199001-78e928502994',
    'https://images.unsplash.com/13/unsplash_5243a2eb2bc02_1.JPG',
    'https://images.unsplash.com/12/green.jpg'
]

new_year = [
    'https://images.unsplash.com/photo-1482932542078-12df7104cc78',
    'https://images.unsplash.com/photo-1482329833197-916d32bdae74',
    'https://images.unsplash.com/photo-1479722842840-c0a823bd0cd6',
    'https://images.unsplash.com/photo-1477915737647-b5246ee6de6f',
    'https://images.unsplash.com/photo-1478401925243-937bbe0eb564',
    'https://images.unsplash.com/photo-1437482184784-6d2e6d6e8844',
    'https://images.unsplash.com/photo-1429962714451-bb934ecdc4ec',
    'https://images.unsplash.com/photo-1467810563316-b5476525c0f9',
    'https://images.unsplash.com/photo-1483489571097-0ca295ba3d08',
    'https://images.unsplash.com/photo-1483122323774-39d65b99613f',
    'https://images.unsplash.com/photo-1482433504097-a3cde62f7882',
    'https://images.unsplash.com/photo-1482444624132-e31f63495df6',
    'https://images.unsplash.com/photo-1482386383748-b78a9840bec9',
    'https://images.unsplash.com/photo-1481653996425-4c3eba04d5e3',
    'https://images.unsplash.com/photo-1480732149909-d4e710a0f81c',
]

love = [
    'https://images.unsplash.com/photo-1461009209120-103a8f970745',
    'https://images.unsplash.com/photo-1426543881949-cbd9a76740a4',
    'https://images.unsplash.com/photo-1480288628948-3a6487c469cf',
    'https://images.unsplash.com/photo-1459259191495-52eccde892c7',
    'https://images.unsplash.com/photo-1475045386676-a66b44a0ff45',
    'https://images.unsplash.com/photo-1465711403138-162e171bb7e4',
    'https://images.unsplash.com/photo-1453175324447-6864b23ecf23',
    'https://images.unsplash.com/photo-1445098693232-134135a7ccfa',
    'https://images.unsplash.com/photo-1475045386676-a66b44a0ff45'
]

jianshi_images = love


def get_unsplash_url(width, height):
    index = random.randint(0, len(jianshi_images) - 1)
    raw_url = jianshi_images[index]
    width = str(width)
    height = str(height)
    modified_url = raw_url + '?fit=crop&w=' + width + '&h=' + height
    return modified_url
