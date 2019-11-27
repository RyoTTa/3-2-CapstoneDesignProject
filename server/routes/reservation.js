
var ObjectId =require('mongodb').ObjectId;

module.exports = function(router,app) {
    console.log('reservation 호출');

    router.route('/reservation').post(function(req,res){
        console.log('reservation 등록 요청');
        var database = app.get('database');
        var _id = req.body._id;
        var borrower = req.body.borrower;
        var start_date =req.body.start_date;
        var end_date = req.body.start_date;

        database.ItemModel.findOne({'_id':ObjectId(_id)},function(err,item){
            item.reservation = true;
            item.borrower = borrower;
            item.save(function(err){
                if(err){
                    throw err;
                }
            });
            var reservation = new database.ReservationModel({
                'item_id' : _id,
                'owner_email' : item.owner_email,
                'borrower_email' : borrower,
                'start_date' : new Date(start_date),
                'end_date' : new Date(end_date)

            });
            reservation.save(function(err){
                if(err){
                    throw err;
                }
            });
        });
        console.log('reservation 등록');
        res.send('true');
        });

    
    router.route('/complete').post(function(req,res){
        console.log('complete 반납 요청');
        var database = app.get('database');
        var item_id = req.body.item_id;
        console.log(item_id);
        database.ReservationModel.findOne({'item_id':ObjectId(item_id)},function(err,reservation){
            console.log(reservation);
                database.ItemModel.findOne({'_id':ObjectId(reservation.item_id)},function(err,item){
                item.reservation = false;
                item.borrower = "";
                item.save(function(err){
                    if(err)
                        throw err;
                    else{
                        database.ReservationModel.remove({'_id':ObjectId(reservation_id)},function(err,doc){
                            if(err){
                                throw err;
                            }
                        });
                    }
                });
                
            });
        });
        
        res.send("true");
    });
};
