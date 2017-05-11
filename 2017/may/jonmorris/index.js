// Library imports
const fs = require('fs');
const zlib = require('zlib');
const csv = require('csvtojson');

// Path to dataset file
const SOURCE_FILE = '../street_signs.csv.gz';
const OUTPUT_FILE = './street_signs.json';

// Create the input file stream
const input = fs.createReadStream(SOURCE_FILE)
    .pipe(zlib.createGunzip());

// Pass the input file stream (unzipped) into the csv parser
csv()
    .fromStream(input)
    .transf((json, csv, i) => {
        let row;

        // TODO: this could be better
        const NESTED_TABLE_PATTERN = /<table[^>]?.*<table[^>]?(.*)<\/table>.*<\/table>/ig;
        const match = NESTED_TABLE_PATTERN.exec(json.Description);
        if (match && match[1]) {
            const table = match[1];

            const NESTED_TABLE_ROWS_PATTERN = /(<td[^>]?([^<]+)<\/td>)/ig;
            let lastKey;
            while (row = NESTED_TABLE_ROWS_PATTERN.exec(table)) {
                // Clean the data
                // The regex leaves a '>' at the beginning so we strip that off
                let value = row[2].substring(1);

                // Nulls come through as '&lt;Null&gt;'. Detect that and make it actually null.
                if (value === '&lt;Null&gt;') value = null;

                if (lastKey) {
                    json[lastKey] = value;
                    lastKey = null;
                } else {
                    lastKey = value;
                }
            }
        }
    })
    .on('error', (err) => {
        console.error(err);
    })
    .on('end_parsed', (json) => {
        // console.log('done', json);

        // Write JSON back out to the fs
        const data = JSON.stringify(json, null, 2);
        fs.writeFileSync(OUTPUT_FILE, data);
    });
