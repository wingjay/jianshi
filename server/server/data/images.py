import random

images = [
'https://images.unsplash.com/photo-1462524500090-89443873e2b4',
'https://images.unsplash.com/photo-1462524500090-89443873e2b4',
'https://images.unsplash.com/12/green.jpg',
'https://images.unsplash.com/photo-1475260231698-62391846c9d5',
'https://images.unsplash.com/photo-1452850387937-f79e4c063cf2',
'https://images.unsplash.com/photo-1428342109953-409d2876cdab',
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

]

candidates = [
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
    'https://images.unsplash.com/photo-1428342109953-409d2876cdab',
    'https://images.unsplash.com/photo-1452850387937-f79e4c063cf2',
    'https://images.unsplash.com/photo-1475260231698-62391846c9d5',
    'https://images.unsplash.com/photo-1450740199001-78e928502994',
    'https://images.unsplash.com/13/unsplash_5243a2eb2bc02_1.JPG',
    'https://images.unsplash.com/12/green.jpg'
]


def get_unsplash_url(index, width, height):
    if index >= len(images) or index < 0:
        index = random.randint(0, len(images) - 1)
    raw_url = images[index]
    width = str(width)
    height = str(height)
    modified_url = raw_url + '?fit=crop&w=' + width + '&h=' + height
    return modified_url
