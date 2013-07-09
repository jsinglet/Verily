<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Error in Method &middot; PwE</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">

    <!-- CSS -->
    <link href="/css/bootstrap.min.css" rel="stylesheet">
    <style type="text/css">

        /* Sticky footer styles
        -------------------------------------------------- */

        html,
        body {
        height: 100%;
        /* The html and body elements cannot have any padding or margin. */
        }

        /* Wrapper for page content to push down footer */
        #wrap {
        min-height: 100%;
        height: auto !important;
        height: 100%;
        /* Negative indent footer by it's height */
        margin: 0 auto -60px;
        }

        /* Set the fixed height of the footer here */
        #push,
        #footer {
        height: 60px;
        }
        #footer {
        background-color: #f5f5f5;
        }

        /* Lastly, apply responsive CSS fixes as necessary */
        @media (max-width: 767px) {
        #footer {
        margin-left: -20px;
        margin-right: -20px;
        padding-left: 20px;
        padding-right: 20px;
        }
        }


        /* Custom page CSS
        -------------------------------------------------- */
        /* Not required for template or sticky footer method. */

        .container {
        width: auto;
        max-width: 680px;
        }
        .container .credit {
        margin: 20px 0;
        }

    </style>

    <link rel="shortcut icon" href="/ico/favicon.png">
</head>

<body>


<!-- Part 1: Wrap all page content here -->
<div id="wrap">

    <!-- Begin page content -->
    <div class="container">
        <div class="page-header">
            <h1>Error While Executing Method</h1>
        </div>
        <p class="lead">The Method you accessed exists but it encountered an error while executing. You can see the PwE
            logs for more information, but the specific error was: </p>

        <p class="lead" align="center"><code>${message}</code></p>


        <p><strong>Hint</strong> You can override this message by placing a template called <code>500.ftl</code> in the
            <code>resources</code> directory of your application. </p>
    </div>

    <div id="push"></div>
</div>

<div id="footer">
    <div class="container">
        <p class="muted credit">Page rendered by <a href="">PwE</a> version ${version}.</p>
    </div>
</div>


<script src="/js/bootstrap.min.js"></script>

</body>
</html>
