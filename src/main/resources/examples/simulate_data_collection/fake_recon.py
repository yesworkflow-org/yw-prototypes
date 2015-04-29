import os

next_resource_id = 1

def resource_facts(file):
    global next_resource_id
    if os.path.isfile(file):
        print "resource({0}, '{1}').".format(next_resource_id, file)
        next_resource_id += 1
    elif os.path.isdir(file):
        dir = file
        subdirs = []
        for item in sorted(os.listdir(dir)):
            path = os.path.join(dir, item)
            if (os.path.isfile(path)):
                resource_facts(path)
            else:
                subdirs.append(path)
        for subdir in subdirs:
           resource_facts(subdir)

def resource_channel_fact(file_id, channel_id):
    print "resource_channel({0}, {1}).".format(file_id, channel_id)

def resource_channel_facts(file_id_first, file_id_last, channel_id):
    for file_id in range(file_id_first, file_id_last + 1):
        resource_channel_fact(file_id, channel_id);

def variable_value_fact(resource_id, variable_id, variable_value):
    print "uri_variable_value({0}, {1}, '{2}').".format(resource_id, variable_id, variable_value)

if __name__ == '__main__':

    print '\n% FACT: resource(resource_id, resource_uri).'
    resource_facts('./calibration.img')
    resource_facts('./cassette_q55_spreadsheet.csv')
    resource_facts('./run')

    print '\n% FACT: resource_channel(resource_id, channel_id).'
    resource_channel_fact(1, 23)
    resource_channel_fact(2, 9)
    resource_channel_fact(3, 3)
    resource_channel_fact(4, 4)
    resource_channel_fact(5, 2)
    resource_channel_facts(6, 139, 1)
    resource_channel_facts(140, 273, 22)

    print '\n% FACT: uri_variable_value(resource_id, variable_id, variable_value).'
    
