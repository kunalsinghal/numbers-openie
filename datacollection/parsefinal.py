inp = file("datacollection/final")

for line in inp:
    if line.strip() != "":
        print line.strip()
