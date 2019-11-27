
var ObjectId = require('mongodb').ObjectId;
var fs = require('fs');

module.exports = function(router,app) {
    console.log('item_update 호출');

    router.route('/item_update').post(function(req,res){
        console.log('item update 요청');
        var database = app.get('database');
        var _id = req.body._id;
        var email = req.body.executed_user;
        var old_image_path = req.body.old_image_path;
        var path = "./images/";
        path=path.concat(old_image_path);

        database.ItemModel.findOne({'_id':ObjectId(_id)},function(err,item){
            if(item.owner_email==email)
            {
                item.name = req.body.name;
                item.price_per_date = req.body.price_per_date;
                item.latitude = req.body.latitude;
                item.longitude = req.body.longitude;
                item.available_date_start = new Date(req.body.available_date_start);
                item.available_date_end = new Date(req.body.available_date_end);
                item.image_path = req.body.new_image_path;
                item.category = req.body.category;
                item.contents = req.body.contents;
            }
            fs.unlink(path,function(err){
                if(err)
                    throw err;
            });
            item.save(function(err){
                if (err) {
                    res.send('false');
                    throw err;
                }
                console.log(' item update');
                res.send('true');
            });

        });
       
    });

};
