use std::time::Instant;
use std::fs::File;
use std::io::Write;
use postgres::{Client, NoTls, Statement, types::Oid};
use prometheus::{Opts, Counter, Registry};
use uuid::Uuid;

fn main() {
    


    let mut client = Client::connect("host=localhost user=synapse dbname=synapse", NoTls).unwrap();

    let stmt = client.prepare("
    insert into domain_event_entry (
        global_index, 
        meta_data, 
        payload, 
        payload_type, 
        time_stamp, 
        aggregate_identifier, 
        sequence_number, 
        type, 
        event_identifier
    ) values ($1, $2, $3, $4, $5, $6, $7, $8, $9)
    ").unwrap();

    let a = 46415271_usize as Oid;

    let mut f = File::create("./output.csv").expect("Unable to create file");
    let batchsize = 1000;

    for b in 0 ..1000{
        let start = Instant::now();
        for i in 0 .. batchsize {
            client.execute(&stmt, 
                &[
                    &(57_000_000_i64+i+b*batchsize),
                    &a,
                    &a,
                    &"dev.amann.synapse.domain.CardIssuedEvent",
                    &"2021-12-10T14:00:35.796624373Z",
                    &Uuid::new_v4().to_string(),
                    &0_i64,
                    &"GiftCard",
                    &Uuid::new_v4().to_string(),
                ]).unwrap();    
        }
        let timediff = start.elapsed().as_millis();
        let line = format!("{},{},{}\n", b, batchsize, timediff);
        f.write_all(line.as_bytes()).expect("Unable to write data");

    }

    


}
