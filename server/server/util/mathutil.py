import time
import math
import sys
import re


def get_age_from_birthday(birthday):
    if birthday:
        return int((int(time.time()) - birthday)/86400/365)
    else:
        return 0 


def str_version_tuple(ss):
    if not ss:
        return tuple([0, 0, 0])
    m = re.match("[^0-9]*(\d+\.\d+(\.\d+)?)", ss)
    s = m.group(1) if m else ''
    if s == '':
        return tuple([0, 0, 0])
    _s = s[1:] if s[0] == 'v' or s[0] == 'V' else s
    return tuple(map(int, (_s.split('.'))))


def num_version_tuple(n):
    if n == 0:
        return tuple([0, 0, 0])
    l, _n = [], n
    while _n != 0:
        l.append(_n % 100) 
        _n = int(_n / 100)
    l.reverse()
    return tuple(l) if l else tuple([0])


def version_tuple(v):
    try:
        v = int(v)
    except:
        pass
    return num_version_tuple(v) if isinstance(v, (int, long)) \
        else str_version_tuple(v)


def num_version(n):
    if isinstance(n, (int, long)):
        return n
    
    if isinstance(n, str):
        _t = str_version_tuple(n)
    else:
        # tuple
        _t = n
    v = 0
    for x in _t:
        v = v * 100 + int(x)
    return v


def str_version(s):
    if isinstance(s, str):
        return s

    if isinstance(s, (int, long)):
        _t = num_version_tuple(s)
    else:
        _t = s
    nums = [str(ver) for ver in _t]
    return '.'.join(nums)


def compare_version(v1, v2):
    """
        We have two types of versions, 
        one is string, like - xx.xx.xx
        two is a number     - xxxxxx
    """
    def _version_tuple_compare(t1, t2):
        """
            directly compare two tuples has a problem that (2,3) < (2,3,0)
            but v2.3 = v2.3.0
        """
        _t1, _t2 = t1, t2
        if len(t1) < len(t2):
            _t1 = tuple(list(t1) + map(lambda x:0, range(0, len(t2)-len(t1))))
        elif len(t1) > len(t2):
            _t2 = tuple(list(t2) + map(lambda x:0, range(0, len(t1)-len(t2))))
        if _t1 == _t2:
            return 0
        else:
            return 1 if _t1 > _t2 else -1

    _v1 = version_tuple(v1)
    _v2 = version_tuple(v2)
    return _version_tuple_compare(_v1, _v2)


def version_gt(v1, v2):
    return compare_version(v1, v2) == 1


def version_ge(v1, v2):
    return compare_version(v1, v2) in [0, 1]


def version_lt(v1, v2):
    return compare_version(v1, v2) == -1


def version_le(v1, v2):
    return compare_version(v1, v2) in [0, -1]


def two_point_distance(begin_long, begin_lati, end_long, end_lati):
    TWO_P_DIS_LIMIT = 0.03
    PI              = 3.14159265359
    rad_factor      = PI / 180.0
    km_to_mile      = 0.621371
    eR              = 6378.245 * km_to_mile 

    diff_e = abs(begin_long - end_long)
    diff_n = abs(begin_lati - end_lati)
    if diff_e <= TWO_P_DIS_LIMIT and diff_n <= TWO_P_DIS_LIMIT:
        d = ((diff_e**2 + diff_n**2)**0.5) * rad_factor
        return d * eR
    else:
        a0 = (90-end_lati)*rad_factor
        b0 = (90-begin_lati)*rad_factor
        c0 = diff_e * rad_factor
        t1 = math.cos(a0) * math.cos(b0) + math.sin(a0) * math.sin(b0) * math.cos(c0)
        t2 = math.acos(t1)
        return t2 * eR


# Returns the android version code from a string, return max int if no version supplied
def get_android_version_code(version):
    if not version:
        return sys.maxint

    m = re.match(r'(?P<major>\d+)\.(?P<minor>\d)\.(?P<patch>\d)', version)
    if not m:
        return 0

    d = m.groupdict()
    return int(d['major']) * 100 + int(d['minor']) * 10 + int(d['patch'])


# Converts an Android version code to string
def android_version_code_to_str(version_code):
    major = version_code / 100
    minor = (version_code - major * 100) / 10
    patch = version_code - major * 100 - minor * 10
    return '.'.join([str(major), str(minor), str(patch)])


def is_pow_of_10(n):
    if n <= 0:
        return False
    logn = math.log10(n)
    return logn == math.floor(logn)
