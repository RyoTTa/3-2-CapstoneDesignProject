
module.exports = function(router,app) {
    console.log('item_insert 호출');

    router.route('/item_insert').post(function(req,res){
        console.log('item 등록 요청');
        var database = app.get('database');
        var item = new database.ItemModel({
            'name' : req.body.name,
            'price_per_date' : req.body.price_per_date,
            'latitude' : req.body.latitude,
            'longitude' : req.body.longitude,
            'available_date_start' : new Date(req.body.available_date_start),
            'available_date_end' : new Date(req.body.available_date_end),
            'image_path' : req.body.image_path,
            'category' : req.body.category,
            'contents' : req.body.contents,
            'owner_email' : req.body.owner_email,
            'reservation' : false
        });
        item.save(function(err){
            if (err) {
                res.send('false');
                throw err;
            }
            console.log('새로운 item 추가');
            res.send('true');
        });
    });

};
