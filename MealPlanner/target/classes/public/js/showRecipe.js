$(document).ready(function() {
    $.ajaxSetup({
      contentType: "application/json; charset=utf-8"
    });


    var loadUsers = function () {
        $.ajax({
            url: "/recipe/"
        }).then(function(data) {
            var html = '<table class="table table-bordered"><tr><th>Day of week</th><th>Recipe</th><th>Tag</th><th width="15%">Actions</th></tr>';

            for(var i in data) {
                var recipe = data[i];

                html += '<tr><td>' + recipe.dayOfWeek + '</td><td>' + recipe.content + '</td><td>' + recipe.searchTag + '</td><td>'
                + '<button type="button" class="btn btn-default edit-button" data-id="' + recipe.id + '"><span class="glyphicon glyphicon-pencil"></span></button>&nbsp;'
                + '<button type="button" class="btn btn-default group-button" data-id="' + recipe.id + '"><span class="glyphicon glyphicon-list-alt"></span></button>&nbsp;'
                + '<button type="button" class="btn btn-default remove-button" data-id="' + recipe.id + '"><span class="glyphicon glyphicon-trash"></span></button>&nbsp;'
                + '</td></tr>'
            }

            html += '</table>'




            $('#recipe').html(html);


            $('.remove-button').click(function(){
                 var id = $(this).data("id");

                  $.ajax({
                      url: '/recipe/' + id,
                      type: 'DELETE',
                      success: function(result) {
                            loadUsers();
                      }
                  });
            });

            $('.edit-button').click(function(){
                var id = $(this).data("id");

                $.ajax({
                    url: "/recipe/" + id
                }).then(function(data) {
                    $('#edit-dayOfWeek').val(data.dayOfWeek);
                    $('#edit-recipe').val(data.content);
                    $('#edit-tag').val(data.searchTag);
                    $('#edit-id').val(data.id);

                    $('#editModal').modal('show')
                });
            });


        });
    };

    loadUsers();

     $('#add-recipe-button').click(function(event) {
            event.preventDefault();
            var addDay =  $('#addDay').val();
            var addRecipe = $('#addRecipe').val();
            var addTag = $('#addTag').val();

            $.post("/recipe/", '{ "dayOfWeek": "' + addDay + '","content": "' + addRecipe + '","searchTag": "' + addTag + '" }').done(function( data ) {
                loadUsers()
            });
     });

      $('#edit-recipe-button').click(function(){
             event.preventDefault();

             var id = $('#edit-id').val();
             console.log("my id is " + id);

             var editDay = $('#edit-dayOfWeek').val();
             var editRecipe = $('#edit-recipe').val();
             var editTag = $('#edit-tag').val();


             $.ajax({
                 url: '/recipe/' + id,
                 type: 'PUT',
                 data: '{"dayOfWeek": "' + editDay + '", "content": "' + editRecipe + '", "searchTag": "' + editTag  + '" }',
                 success: function(result) {
                     loadUsers();
                     $('#edit-form').hide();
                 }
             });
      });



});

