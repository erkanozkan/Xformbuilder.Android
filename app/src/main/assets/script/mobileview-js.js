


$(document).ready(function () {

    var lang = $('#lang')[0].value;
            $('input[type="file"]').change(function () {
           var value = null;
           if(value != null){

           var array = new Array();
           array = value.split("$^^$^^$");

           $(this).attr('data-val-type',array[1]);

           var divId = $(this)[0].parentElement.parentElement.parentElement.id;

        var input = "<input type='button'  onclick=CallView(this)  id='btnView_"+divId+"'    value='" + $('#viewText')[0].value + "' class='btn btn-primary btn-file'    />";

        if ($('#btnView_' + divId)[0] != undefined) {
                     $('#btnView_' + divId).remove();
                     $('#' + divId).append(input);

                 }
                 else{

                     $('#' + divId).append(input);
                  }

      if ($('form').validate().errorList.length == 0 && $('form').valid()) {
                               if (validateCheckBoxElement())
                               {
                               Submit(1, "0", "1");
                               }
                                else{
                                     Submit(0, "0", "1");
                               }
                            }
                            else {
                                Submit(0,"1","1"); //form eksik dolduruldu ise
                            }
               }


                });

    if (lang == "tr-TR") {
        $.extend($.validator.messages, {
            required: 'Bu alanın doldurulması zorunludur.',
            remote: 'Lütfen bu alanı düzeltin.',
            email: 'Lütfen geçerli bir e-posta adresi giriniz.',
            url: 'Lütfen geçerli bir web adresi (URL) giriniz.',
            date: 'Lütfen geçerli bir tarih giriniz.',
            dateISO: 'Lütfen geçerli bir tarih giriniz(ISO formatında)',
            number: 'Lütfen geçerli bir sayı giriniz.',
            digits: 'Lütfen sadece sayısal karakterler giriniz.',
            creditcard: 'Lütfen geçerli bir kredi kartı giriniz.',
            equalTo: 'Lütfen aynı değeri tekrar giriniz.',
            extension: 'Lütfen geçerli uzantıya sahip bir değer giriniz.',
            maxlength: $.validator.format('Lütfen en fazla {0} karakter uzunluğunda bir değer giriniz.'),
            minlength: $.validator.format('Lütfen en az {0} karakter uzunluğunda bir değer giriniz.'),
            rangelength: $.validator.format('Lütfen en az {0} ve en fazla {1} uzunluğunda bir değer giriniz.'),
            range: $.validator.format('Lütfen {0} ile {1} arasında bir değer giriniz.'),
            max: $.validator.format('Lütfen {0} değerine eşit ya da daha küçük bir değer giriniz.'),
            min: $.validator.format('Lütfen {0} değerine eşit ya da daha büyük bir değer giriniz.')
        });
    } else {
        $.extend($.validator.messages, {
            required: 'This field is required.',
            remote: 'Please fix this field.',
            email: 'Please enter a valid email address.',
            url: 'Please enter a valid URL.',
            date: 'Please enter a valid date.',
            dateISO: 'Please enter a valid date ( ISO ).',
            number: 'Please enter a valid number.',
            digits: 'Please enter only digits.',
            creditcard: 'Please enter a valid credit card number.',
            equalTo: 'Please enter the same value again.',
            maxlength: $.validator.format('Please enter no more than {0} characters.'),
            minlength: $.validator.format('Please enter at least {0} characters.'),
            rangelength: $.validator.format('Please enter a value between {0} and {1} characters long.'),
            range: $.validator.format('Please enter a value between {0} and {1}.'),
            max: $.validator.format('Please enter a value less than or equal to {0}.'),
            min: $.validator.format('Please enter a value greater than or equal to {0}.')
        });
    };




    $(document).on('mouseenter', '.NewStarLi', function () {
        var value = $(this)[0].id.split('_')[1];
        var DivId = $(this)[0].parentElement.parentElement.id;
        var ElementId = $(this)[0].parentElement.parentElement.children[2].value.split('_')[1];
        $('#elementDiv_' + ElementId + '_RatingValue')[0].value = value;
        for (var i = 1; i <= 5; i++) {
            $('#' + DivId + ' li[id=star_' + i + ']').removeClass('AddStar');
            $('#' + DivId + ' li[id=star_' + i + ']').css('color', 'black');
        }
        for (var i = 1; i <= value; i++) {
            $('#' + DivId + ' li[id=star_' + i + ']').removeClass('AddStar');
            $('#' + DivId + ' li[id=star_' + i + ']').addClass('AddStar');
            $('#' + DivId + ' li[id=star_' + i + ']').css('color', 'transparent');
        }
    })
        .on('mouseleave', '.NewStarLi', function () {
            $(document).addClass('NormalCursor');

        });


    function validatePhone(value) {
        var expression = /(\+?\d[- .]*){7,13}/;
        var regex = new RegExp(expression);
        return value.match(regex);
    };

    function validateURL(value) {
        var expression = /((?:https?\:\/\/|www\.)(?:[-a-z0-9]+\.)*[-a-z0-9]+.*)/i;
        var regex = new RegExp(expression);
        return value.match(regex);
    }

    $.validator.addMethod('urlvalidation', function (value, element) {
        return this.optional(element) || validateURL(value);
    }, $('#urlvalidate')[0].value );
    $.validator.addMethod('phonevalidation', function (value, element) {
        return this.optional(element) || validatePhone(value);
    }, $('#phonevalidate')[0].value);

$('form').on("keypress", function(e) {
  var code = e.keyCode || e.which;
  if (code  == 13) {
     $('form').validate({
               rules: {
                   urlvalidation: {
                       urlvalidation: true
                   },
                   phonevalidation: {
                       phonevalidation: true
                   }
               }
           });
           if ($('form').validate().errorList.length == 0 && $('form').valid()) {
                     if (validateCheckBoxElement())
                                 {
                     Submit(1, "1", "0"); } else {

               Submit(0,"1","0");
                     }
           }
           else {
           Submit(0,"1","0");
           }
e.preventDefault();
    return false;
  }
});
    $('form').submit(function (e) {

        $('form').validate({
            rules: {
                urlvalidation: {
                    urlvalidation: true
                },
                phonevalidation: {
                    phonevalidation: true
                }
            }
        });

        if ($('form').validate().errorList.length == 0 && $('form').valid()) {
            if (validateCheckBoxElement())
            {

             Submit(1, "0", "0");
            }
            else
            {

            Submit(0, "0", "0");
            }

        }
        else {

            Submit(0,"0","0"); // form eksik dolduruldu ise
        }

        return false;
    });




    function Submit(isUploadable,keyCode,forFile) {

        $('[type=text],[type=email],[type=date],[type=url],[type=tel],[type=file],[type=number],[type=hidden], textarea').each(function () {
            this.defaultValue = this.value;
         });

          $('[type=button]').each(function () {
                $(this).attr("onclick","CallView(this)");
         });

        $('[type=checkbox], [type=radio]').each(function () {
            this.defaultChecked = this.checked;
        });
        $('select option').each(function () {
            this.defaultSelected = this.selected;
        });

        var html = $('html').html();
        var json = '';
        var mobileFieldArray = new Array();
        var field1_title = '',
            field1_value = '',
            field2_title = '',
            field2_value = '',
            field3_title = '',
            field3_value = '';
        var fieldDiv = $('.field');



        for (var i = 0; i < fieldDiv.length; i++) {

            if (fieldDiv[i].attributes['data-val-type'] == undefined)
                continue;

            var type = fieldDiv[i].attributes['data-val-type'].value
            var elementId = fieldDiv[i].attributes['data-val-id'] != undefined ? fieldDiv[i].attributes['data-val-id'].value : '';
            var formId = fieldDiv[i].attributes['data-val-formId'] != undefined ? fieldDiv[i].attributes['data-val-formId'].value : '';

            if (type != 'form' && (i == 1 || i == 2 || i == 3)) {
                if (type == 'name') {
                    switch (i) {
                        case 1:
                            field1_title = fieldDiv[i].children[0].innerText.replace('*', '');
                            field1_value = fieldDiv[i].children[1].value.toLowerCase() + ' ' + fieldDiv[i].children[2].value.toLowerCase();
                            break;
                        case 2:
                            field2_title = fieldDiv[i].children[0].innerText.replace('*', '');
                            field2_value = fieldDiv[i].children[1].value.toLowerCase() + ' ' + fieldDiv[i].children[2].value.toLowerCase();
                            break;
                        case 3:
                            field3_title = fieldDiv[i].children[0].innerText.replace('*', '');
                            field3_value = fieldDiv[i].children[1].value.toLowerCase() + ' ' + fieldDiv[i].children[2].value.toLowerCase();
                            break;
                        default:
                            break;
                    }
                }
                if (type == 'email' || type == 'tel' || type == 'url' || type == 'text' || type == 'textarea') {
                    switch (i) {
                        case 1:
                            field1_title = fieldDiv[i].children[0].innerText.replace('*', '');
                            field1_value = fieldDiv[i].children[1].value.toLowerCase();
                            break;
                        case 2:
                            field2_title = fieldDiv[i].children[0].innerText.replace('*', '');
                            field2_value = fieldDiv[i].children[1].value.toLowerCase();
                            break;
                        case 3:
                            field3_title = fieldDiv[i].children[0].innerText.replace('*', '');
                            field3_value = fieldDiv[i].children[1].value.toLowerCase();
                            break;
                        default:
                            break;
                    }
                }
            }
            var mobileObject = new Object();
            switch (type) {
                case 'form':
                    mobileObject = {
                        'type': type,
                        'value': fieldDiv[i].children[0].innerText,
                        'id': formId
                    };
                    break;
                case 'text':
                case 'email':
                case 'number':
                case 'date':
                case 'tel':
                case 'url':
                case 'textarea':
                case 'rating':

                    mobileObject = {
                        'type': type,
                        'value': fieldDiv[i].children[1].value,
                        'id': elementId
                    };
                    break;
                case 'signature':
                    mobileObject = {
                        'type': type,
                        'value': fieldDiv[i].children[2].value,
                        'id': elementId
                    };
                    break;
                case 'link':
                    mobileObject = {
                        'type': type,
                        'value': fieldDiv[i].children[1].innerText,
                        'id': elementId
                    };
                    break;
                case 'section':
                    mobileObject = {
                        'type': type,
                        'value': fieldDiv[i].children[1].innerText,
                        'id': elementId
                    };
                    break;
                case 'file':
                         mobileObject = {
                            'type': type,
                            'data_val_type':fieldDiv[i].children[1].children[0].children[0].attributes['data-val-type'] != undefined ?  fieldDiv[i].children[1].children[0].children[0].attributes['data-val-type'].value : '',
                            'value':fieldDiv[i].children[1].children[0].children[0].attributes['data-val-value'] != undefined ?  fieldDiv[i].children[1].children[0].children[0].attributes['data-val-value'].value : '',
                            'id': elementId
                        };
                    break;
                case 'select':
                    var selectValue = $('#' + fieldDiv[i].id + ' select')[0].value;
                    mobileObject = {
                        'type': type,
                        'value': selectValue,
                        'id': elementId,
                        'optId': $('#' + fieldDiv[i].id + ' select')[0].options[$('#' + fieldDiv[i].id + ' select')[0].selectedIndex].id
                    };
                    break;
                case 'checkbox':
                case 'radio':

                    var checkDiv = $('#' + fieldDiv[i].id + ' div');
                    mobileObject = {
                        'type': type,
                        'value': '',
                        'id': elementId
                    };

                    var optionObject = new Object();
                    mobileObject.Options = new Array();

                    for (var c = 0; c < checkDiv.length; c++) {

                        if (checkDiv[c].children[0].checked == true) {
                            optionObject = {
                                'data_val_checked': checkDiv[c].children[0].checked,
                                'text': checkDiv[c].children[1].innerText,
                                'id': checkDiv[c].children[0].id.split('_')[2]
                            };

                            mobileObject.Options.push(optionObject);
                        }
                    }
                    break;
                case 'name':
                case 'price':
                    mobileObject = {
                        'type': type,
                        'value': '',
                        'id': elementId
                    };
                    var optionObject = new Object();
                    mobileObject.Options = new Array();
                    for (var n = 1; n <= 2; n++) {

                        optionObject = {
                            'type': type,
                            'value': fieldDiv[i].children[n].value,
                            'id': fieldDiv[i].children[n].attributes['id'] != undefined ? fieldDiv[i].children[n].attributes['id'].value : ''
                        };
                        mobileObject.Options.push(optionObject);
                    }
                    break;
                case 'adress':
                    mobileObject = {
                        'type': type,
                        'value': '',
                        'id': elementId
                    };
                    var optionObject = new Object();
                    mobileObject.Options = new Array();
                    for (var a = 1; a <= 4; a++) {
                        optionObject = {
                            'value': fieldDiv[i].children[a].value,
                            'id': fieldDiv[i].children[a].attributes['id'] != undefined ? fieldDiv[i].children[a].attributes['id'].value : ''
                        };
                        mobileObject.Options.push(optionObject);
                    }
                    break;
                default:
                    break;
            }
            mobileFieldArray.push(mobileObject);
        }

        json = JSON.stringify(mobileFieldArray);
        var mobileDivHtml = $('#mobilediv').html();


      $('input[type="submit"]')[0].value = "Kaydediliyor...";
      $('input[type="submit"]').attr('disabled','disabled');

      app.FormSubmit(html, json, isUploadable,keyCode ,forFile,field1_title, field1_value, field2_title, field2_value, field3_title, field3_value);

      $('input[type="submit"]').removeAttr('disabled');


     $('input[type="submit"]')[0].value = $('#submitText')[0].value;
         return false;
    }

});

  function CallView(sender){
       var id = sender.id;

       app.ViewFile(base64File);
        }

        function validateCheckBox(element) {

            var size = $('#' + element.id + " input[type='checkbox']:checked").length;
            if ($("#divError_" + element.id) != undefined) {
                $("#divError_" + element.id).remove();
            }
            if (size == 0) {
                $(element).append("<label id='divError_" + element.id + "' class='errorDiv' style='background-color: #dbdbdb;'> Please select one at least. </label>");
                return false;
            };

            return true;
        }

        function validateCheckBoxElement() {

            var returnValueArray = new Array();
            $('div.require-one').each(function (i, e) {

                var rValue = validateCheckBox(e);
                returnValueArray.push(rValue);
            });
            if ($.inArray(false, returnValueArray) != -1)
                return false;
            else
                return true;

        }
